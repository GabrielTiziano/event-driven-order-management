import { test, expect, request } from '@playwright/test';
import { randomUUID } from 'node:crypto';

// URLs dos servicos (sobrescreviveis por env). Defaults = portas locais do projeto.
const CLIENTES  = process.env.CLIENTES_URL  ?? 'http://localhost:8082';
const PEDIDOS   = process.env.PEDIDOS_URL   ?? 'http://localhost:8083';
const PRODUTOS  = process.env.PRODUTOS_URL  ?? 'http://localhost:8084';
const LOGISTICA = process.env.LOGISTICA_URL ?? 'http://localhost:8086';
const API_KEY   = process.env.WEBHOOK_PAYMENTS_API_KEY ?? 'chave-secreta-local';

test('ciclo completo: pedido percorre REALIZADO -> ENVIADO via eventos', async () => {
  const api = await request.newContext();

  // 1) cliente (dependencia do snapshot do pedido)
  const cpf = String(Date.now()).slice(-11).padStart(11, '0');
  const codigoCliente = await test.step('cria cliente', async () => {
    const resp = await api.post(`${CLIENTES}/clientes`, {
      data: { nome: 'E2E Cliente', cpf, logradouro: 'Rua E2E', numero: '100', bairro: 'Centro', email: 'e2e@example.com', telefone: '41999990000' },
    });
    expect(resp.ok(), `POST /clientes falhou: ${resp.status()} ${await resp.text()}`).toBeTruthy();
    return (await resp.json()).codigo as number;
  });

  // 2) produto (dependencia do item do pedido)
  const codigoProduto = await test.step('cria produto', async () => {
    const resp = await api.post(`${PRODUTOS}/produtos`, {
      data: { nome: 'E2E Produto', descricao: 'item de teste', preco: 10.00, quantidade: 100 },
    });
    expect(resp.ok(), `POST /produtos falhou: ${resp.status()} ${await resp.text()}`).toBeTruthy();
    return (await resp.json()).codigo as number;
  });

  // 3) pedido -> nasce REALIZADO e ja tem chavePagamento (banco simulado)
  const { codigo, chavePagamento } = await test.step('cria pedido', async () => {
    const resp = await api.post(`${PEDIDOS}/pedidos`, {
      data: {
        codigoCliente,
        metodoPagamento: 'PIX',
        observacoes: 'pedido E2E',
        enderecoEntrega: { logradouro: 'Rua E2E', numero: '100', bairro: 'Centro', cidade: 'Curitiba', cep: '80000000' },
        itens: [{ codigoProduto, quantidade: 2, valorUnitario: 10.00 }],
      },
    });
    expect(resp.status(), `POST /pedidos falhou: ${await resp.text()}`).toBe(201);
    const p = await resp.json();
    expect(p.status).toBe('REALIZADO');
    expect(p.chavePagamento, 'pedido sem chavePagamento').toBeTruthy();
    return { codigo: p.codigo as number, chavePagamento: p.chavePagamento as string };
  });

  // 4) PSP confirma o pagamento (callback idempotente) -> PAGO -> publica pedido-pago
  await test.step('aprova pagamento (callback PSP)', async () => {
    const resp = await api.post(`${PEDIDOS}/webhooks/pagamentos`, {
      headers: { 'X-API-Key': API_KEY, 'Idempotency-Key': randomUUID() },
      data: { codigo, chavePagamento, aprovado: true, observacoes: 'E2E aprovado' },
    });
    expect(resp.ok(), `callback de pagamento falhou: ${resp.status()} ${await resp.text()}`).toBeTruthy();
  });

  // 5) coreografia assincrona: PAGO -> FATURADO (faturamento) -> PREPARANDO_ENVIO (logistica)
  await test.step('aguarda ate PREPARANDO_ENVIO', async () => {
    await expect
      .poll(async () => (await (await api.get(`${PEDIDOS}/pedidos/${codigo}`)).json()).status, {
        message: 'pedido nao chegou em PREPARANDO_ENVIO (faturamento/logistica consumiram?)',
        timeout: 90_000,
        intervals: [1000],
      })
      .toBe('PREPARANDO_ENVIO');
  });

  // 6) comando de despacho -> gera rastreio e publica pedido-enviado
  const rastreioDespacho = await test.step('despacha a entrega', async () => {
    const resp = await api.post(`${LOGISTICA}/entregas/${codigo}/despachar`);
    expect(resp.ok(), `despacho falhou: ${resp.status()} ${await resp.text()}`).toBeTruthy();
    const rastreio = (await resp.json()).codigoRastreio as string;
    expect(rastreio, 'despacho sem codigoRastreio').toBeTruthy();
    return rastreio;
  });

  // 7) pedidos consome pedido-enviado -> ENVIADO + grava o rastreio
  await test.step('aguarda ate ENVIADO com rastreio', async () => {
    await expect
      .poll(async () => {
        const p = await (await api.get(`${PEDIDOS}/pedidos/${codigo}`)).json();
        return { status: p.status, rastreio: p.codigoRastreio };
      }, {
        message: 'pedido nao chegou em ENVIADO com o rastreio gravado',
        timeout: 90_000,
        intervals: [1000],
      })
      .toEqual({ status: 'ENVIADO', rastreio: rastreioDespacho });
  });

  await api.dispose();
});
