package gabriel.tiziano.microservice_pedidos.mapper;

import gabriel.tiziano.microservice_pedidos.dto.EnderecoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoItemRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.Endereco;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoMapperTest {

    @Test
    void toEntity_deveMapearPedidoComItensECalcularTotal() {
        PedidoRequest request = new PedidoRequest(
                1L,
                "Entregar pela manhã",
                MetodoPagamento.CREDITO,
                3,
                new EnderecoRequest("Rua A", "100", "Centro", "Curitiba", "80000000"),
                List.of(
                        new PedidoItemRequest(10L, 2, new BigDecimal("10.00")),
                        new PedidoItemRequest(20L, 3, new BigDecimal("5.00"))
                ));

        Pedido pedido = PedidoMapper.toEntity(request);

        assertThat(pedido.getCodigo()).isNull();
        assertThat(pedido.getCodigoCliente()).isEqualTo(1L);
        assertThat(pedido.getMetodoPagamento()).isEqualTo(MetodoPagamento.CREDITO);
        assertThat(pedido.getParcelas()).isEqualTo(3);
        assertThat(pedido.getEnderecoEntrega()).isNotNull();
        assertThat(pedido.getEnderecoEntrega().getCidade()).isEqualTo("Curitiba");
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.REALIZADO);
        assertThat(pedido.getDataPedido()).isNotNull();
        assertThat(pedido.getTotal()).isEqualByComparingTo("35.00");
        assertThat(pedido.getItens()).hasSize(2);
        assertThat(pedido.getItens().get(0).getCodigoProduto()).isEqualTo(10L);
        assertThat(pedido.getItens().get(0).getPedido()).isSameAs(pedido);
    }

    @Test
    void toResponse_deveMapearPedidoComItens() {
        Pedido pedido = new Pedido();
        pedido.setCodigo(99L);
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setMetodoPagamento(MetodoPagamento.PIX);
        pedido.setTotal(new BigDecimal("35.00"));
        pedido.setObservacoes("obs");
        pedido.setEnderecoEntrega(new Endereco("Rua A", "100", "Centro", "Curitiba", "80000000"));

        ItemPedido item = new ItemPedido();
        item.setCodigo(500L);
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        PedidoResponse response = PedidoMapper.toResponse(pedido);

        assertThat(response.codigo()).isEqualTo(99L);
        assertThat(response.codigoCliente()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(StatusPedido.PAGO);
        assertThat(response.metodoPagamento()).isEqualTo(MetodoPagamento.PIX);
        assertThat(response.total()).isEqualByComparingTo("35.00");
        assertThat(response.enderecoEntrega()).isNotNull();
        assertThat(response.enderecoEntrega().cidade()).isEqualTo("Curitiba");
        assertThat(response.itens()).hasSize(1);
        assertThat(response.itens().get(0).codigo()).isEqualTo(500L);
        assertThat(response.itens().get(0).codigoProduto()).isEqualTo(10L);
        assertThat(response.itens().get(0).quantidade()).isEqualTo(2);
    }
}