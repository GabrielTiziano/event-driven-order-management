package gabriel.tiziano.microservice_pedidos;

import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
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
class RetryPagamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Pedido persistirPedido(StatusPedido status, MetodoPagamento metodo, Integer parcelas) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setMetodoPagamento(metodo);
        pedido.setParcelas(parcelas);
        pedido.setTotal(new BigDecimal("20.00"));
        pedido.setChavePagamento("chave-antiga");

        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        return pedidoRepository.save(pedido);
    }

    private String retryJson(MetodoPagamento metodo, Integer parcelas) {
        String parcelasJson = parcelas == null ? "null" : parcelas.toString();
        return """
                {
                  "metodoPagamento": "%s",
                  "parcelas": %s
                }
                """.formatted(metodo, parcelasJson);
    }

    @Test
    void retry_pedidoComErroPagamento_deveVoltarParaRealizadoEGerarNovaChave() throws Exception {
        Pedido pedido = persistirPedido(StatusPedido.ERRO_PAGAMENTO, MetodoPagamento.PIX, null);

        mockMvc.perform(post("/pedidos/{codigo}/pagamentos", pedido.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(retryJson(MetodoPagamento.PIX, null)))
                .andExpect(status().isAccepted());

        Pedido atualizado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.REALIZADO);
        assertThat(atualizado.getChavePagamento()).isNotNull();
        assertThat(atualizado.getChavePagamento()).isNotEqualTo("chave-antiga");
    }

    @Test
    void retry_trocandoMetodoPagamento_devePersistirNovoMetodo() throws Exception {
        Pedido pedido = persistirPedido(StatusPedido.ERRO_PAGAMENTO, MetodoPagamento.PIX, null);

        mockMvc.perform(post("/pedidos/{codigo}/pagamentos", pedido.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(retryJson(MetodoPagamento.CREDITO, 3)))
                .andExpect(status().isAccepted());

        Pedido atualizado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(atualizado.getMetodoPagamento()).isEqualTo(MetodoPagamento.CREDITO);
        assertThat(atualizado.getParcelas()).isEqualTo(3);
    }

    @Test
    void retry_pedidoForaDeErroPagamento_deveRetornar409() throws Exception {
        Pedido pedido = persistirPedido(StatusPedido.REALIZADO, MetodoPagamento.PIX, null);

        mockMvc.perform(post("/pedidos/{codigo}/pagamentos", pedido.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(retryJson(MetodoPagamento.PIX, null)))
                .andExpect(status().isConflict());

        Pedido inalterado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(inalterado.getStatus()).isEqualTo(StatusPedido.REALIZADO);
    }

    @Test
    void retry_pedidoInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(post("/pedidos/{codigo}/pagamentos", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(retryJson(MetodoPagamento.PIX, null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void retry_creditoSemParcelas_deveRetornar422() throws Exception {
        Pedido pedido = persistirPedido(StatusPedido.ERRO_PAGAMENTO, MetodoPagamento.PIX, null);

        mockMvc.perform(post("/pedidos/{codigo}/pagamentos", pedido.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(retryJson(MetodoPagamento.CREDITO, null)))
                .andExpect(status().isUnprocessableEntity());

        Pedido inalterado = pedidoRepository.findById(pedido.getCodigo()).orElseThrow();
        assertThat(inalterado.getStatus()).isEqualTo(StatusPedido.ERRO_PAGAMENTO);
    }
}