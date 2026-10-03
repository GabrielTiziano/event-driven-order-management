package gabriel.tiziano.microservice_pedidos;

import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.repository.CallbackProcessadoRepository;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CallbackPagamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CallbackProcessadoRepository callbackProcessadoRepository;

    private Pedido persistirPedido(String chavePagamento, StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setMetodoPagamento(MetodoPagamento.PIX);
        pedido.setTotal(new BigDecimal("20.00"));
        pedido.setChavePagamento(chavePagamento);

        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        return pedidoRepository.save(pedido);
    }

    private String callbackJson(Long codigo, String chavePagamento, boolean aprovado) {
        return """
                {
                  "codigo": %d,
                  "chavePagamento": "%s",
                  "aprovado": %b,
                  "observacoes": "teste"
                }
                """.formatted(codigo, chavePagamento, aprovado);
    }

    @Test
    void callback_aprovado_deveMarcarComoPago() throws Exception {
        Pedido pedido = persistirPedido("chave-123", StatusPedido.REALIZADO);

        mockMvc.perform(post("/webhooks/pagamentos")
                        .header("Idempotency-Key", "idem-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackJson(pedido.getCodigo(), "chave-123", true)))
                .andExpect(status().isOk());

        Pedido atualizado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.PAGO);
        assertThat(callbackProcessadoRepository.existsById("idem-1")).isTrue();
    }

    @Test
    void callback_recusado_deveMarcarComoErroPagamento() throws Exception {
        Pedido pedido = persistirPedido("chave-123", StatusPedido.REALIZADO);

        mockMvc.perform(post("/webhooks/pagamentos")
                        .header("Idempotency-Key", "idem-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackJson(pedido.getCodigo(), "chave-123", false)))
                .andExpect(status().isOk());

        Pedido atualizado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.ERRO_PAGAMENTO);
    }

    @Test
    void callback_reentregaComMesmaChaveIdempotencia_naoDeveReprocessar() throws Exception {
        Pedido pedido = persistirPedido("chave-123", StatusPedido.REALIZADO);
        String json = callbackJson(pedido.getCodigo(), "chave-123", true);

        mockMvc.perform(post("/webhooks/pagamentos")
                        .header("Idempotency-Key", "idem-3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/webhooks/pagamentos")
                        .header("Idempotency-Key", "idem-3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Pedido atualizado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    void callback_comChavePagamentoInvalida_deveRetornar422() throws Exception {
        Pedido pedido = persistirPedido("chave-123", StatusPedido.REALIZADO);

        mockMvc.perform(post("/webhooks/pagamentos")
                        .header("Idempotency-Key", "idem-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(callbackJson(pedido.getCodigo(), "chave-errada", true)))
                .andExpect(status().isUnprocessableEntity());

        Pedido inalterado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(inalterado.getStatus()).isEqualTo(StatusPedido.REALIZADO);
    }
}
