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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Pedido persistirPedido(StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setMetodoPagamento(MetodoPagamento.PIX);
        pedido.setTotal(new BigDecimal("20.00"));

        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        return pedidoRepository.save(pedido);
    }

    @Test
    void create_devePersistirPedidoComItensECalcularTotal() throws Exception {
        String json = """
                {
                  "codigoCliente": 1,
                  "observacoes": "obs",
                  "metodoPagamento": "CREDITO",
                  "parcelas": 3,
                  "enderecoEntrega": {
                    "logradouro": "Rua A",
                    "numero": "100",
                    "bairro": "Centro",
                    "cidade": "Curitiba",
                    "cep": "80000000"
                  },
                  "itens": [
                    { "codigoProduto": 10, "quantidade": 2, "valorUnitario": 10.00 },
                    { "codigoProduto": 20, "quantidade": 3, "valorUnitario": 5.00 }
                  ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").isNotEmpty())
                .andExpect(jsonPath("$.status").value("REALIZADO"))
                .andExpect(jsonPath("$.metodoPagamento").value("CREDITO"))
                .andExpect(jsonPath("$.itens.length()").value(2));

        List<Pedido> pedidos = pedidoRepository.findAll();
        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getTotal()).isEqualByComparingTo("35.00");
        assertThat(pedidos.get(0).getMetodoPagamento()).isEqualTo(MetodoPagamento.CREDITO);
        assertThat(pedidos.get(0).getParcelas()).isEqualTo(3);
        assertThat(pedidos.get(0).getEnderecoEntrega().getCidade()).isEqualTo("Curitiba");
    }

    @Test
    void findById_quandoExiste_deveRetornar200() throws Exception {
        Pedido salvo = persistirPedido(StatusPedido.REALIZADO);

        mockMvc.perform(get("/pedidos/{codigo}", salvo.getCodigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(salvo.getCodigo()))
                .andExpect(jsonPath("$.itens.length()").value(1));
    }

    @Test
    void findById_quandoNaoExiste_deveRetornar404() throws Exception {
        mockMvc.perform(get("/pedidos/{codigo}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_comTransicaoValida_devePersistirNovoStatus() throws Exception {
        Pedido salvo = persistirPedido(StatusPedido.REALIZADO);

        String json = """
                { "status": "PAGO" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", salvo.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"));

        Pedido atualizado = pedidoRepository.findById(salvo.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    void updateStatus_comRetentativaDePagamento_devePersistirNovoStatus() throws Exception {
        Pedido salvo = persistirPedido(StatusPedido.ERRO_PAGAMENTO);

        String json = """
                { "status": "PAGO" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", salvo.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"));

        Pedido atualizado = pedidoRepository.findById(salvo.getCodigo()).orElseThrow();
        assertThat(atualizado.getStatus()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    void updateStatus_comTransicaoInvalida_deveRetornar409() throws Exception {
        Pedido salvo = persistirPedido(StatusPedido.REALIZADO);

        String json = """
                { "status": "ENVIADO" }
                """;

        mockMvc.perform(patch("/pedidos/{codigo}/status", salvo.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

        Pedido inalterado = pedidoRepository.findById(salvo.getCodigo()).orElseThrow();
        assertThat(inalterado.getStatus()).isEqualTo(StatusPedido.REALIZADO);
    }

    @Test
    void delete_deveRemoverPedidoEItens() throws Exception {
        Pedido salvo = persistirPedido(StatusPedido.REALIZADO);

        mockMvc.perform(delete("/pedidos/{codigo}", salvo.getCodigo()))
                .andExpect(status().isNoContent());

        assertThat(pedidoRepository.findById(salvo.getCodigo())).isEmpty();
    }
}