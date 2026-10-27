# E2E — ciclo de pedido (Playwright, modo API)

Teste de ponta a ponta **caixa-preta** do fluxo event-driven. Nao usa browser:
dispara chamadas REST contra a stack real e acompanha o pedido mudar de estado
atraves dos eventos no Kafka, de `REALIZADO` ate `ENVIADO`.

## Pre-requisitos (a stack inteira no ar)

1. Infra (nos tres compose de `servicos/`):
   ```bash
   docker compose -f servicos/database/docker-compose.yml up -d
   docker compose -f servicos/broker/docker-compose.yml up -d
   docker compose -f servicos/bucket/docker-compose.yml up -d
   ```
2. Os 5 microservices rodando (cada um em um terminal, com JAVA_HOME 21):
   clientes (8082), pedidos (8083), produtos (8084), faturamento, logistica (8086).
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)
   ./mvnw -pl microservice-clientes spring-boot:run   # e assim por diante
   ```

## Rodar

```bash
cd e2e
PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 npm install   # so o cliente HTTP, sem browsers
npm test
npm run report   # abre o relatorio HTML
```

## Configuracao (opcional)

URLs e chave do webhook sao sobrescriviveis por env:

| Variavel | Default |
| --- | --- |
| `CLIENTES_URL` | `http://localhost:8082` |
| `PEDIDOS_URL` | `http://localhost:8083` |
| `PRODUTOS_URL` | `http://localhost:8084` |
| `LOGISTICA_URL` | `http://localhost:8086` |
| `WEBHOOK_PAYMENTS_API_KEY` | `chave-secreta-local` |

## O que o teste prova

Um pedido criado do zero, so por chamadas externas e eventos reais, percorre
`REALIZADO -> PAGO -> FATURADO -> PREPARANDO_ENVIO -> ENVIADO` atravessando os 5
servicos e o Kafka — sem nenhum mock. E o complemento do teste de integracao
(`CicloPedidoKafkaIntegrationTest`), que valida a fiacao Kafka dentro de um servico.
