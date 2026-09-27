package gabriel.tiziano.microservice_pedidos.mapper;

import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoMapperTest {

    @Test
    void toEntity_deveMapearTodosOsCampos() {
        PedidoRequest request = new PedidoRequest(
                1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "REALIZADO");

        Pedido pedido = PedidoMapper.toEntity(request);

        assertThat(pedido.getCodigo()).isNull();
        assertThat(pedido.getCodigoCliente()).isEqualTo(1L);
        assertThat(pedido.getCodigoProduto()).isEqualTo(2L);
        assertThat(pedido.getQuantidade()).isEqualTo(3);
        assertThat(pedido.getValorUnitario()).isEqualByComparingTo("10.00");
        assertThat(pedido.getTotal()).isEqualByComparingTo("30.00");
        assertThat(pedido.getStatus()).isEqualTo("REALIZADO");
    }

    @Test
    void toResponse_deveMapearTodosOsCampos() {
        Pedido pedido = new Pedido(
                99L, 1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "PAGO");

        PedidoResponse response = PedidoMapper.toResponse(pedido);

        assertThat(response.codigo()).isEqualTo(99L);
        assertThat(response.codigoCliente()).isEqualTo(1L);
        assertThat(response.codigoProduto()).isEqualTo(2L);
        assertThat(response.quantidade()).isEqualTo(3);
        assertThat(response.valorUnitario()).isEqualByComparingTo("10.00");
        assertThat(response.total()).isEqualByComparingTo("30.00");
        assertThat(response.status()).isEqualTo("PAGO");
    }

    @Test
    void updateEntity_deveAtualizarOsCamposMantendoOCodigo() {
        Pedido pedido = new Pedido(
                99L, 1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "REALIZADO");

        PedidoRequest request = new PedidoRequest(
                5L, 6L, 4,
                new BigDecimal("15.00"), new BigDecimal("60.00"),
                "PAGO");

        PedidoMapper.updateEntity(pedido, request);

        assertThat(pedido.getCodigo()).isEqualTo(99L);
        assertThat(pedido.getCodigoCliente()).isEqualTo(5L);
        assertThat(pedido.getCodigoProduto()).isEqualTo(6L);
        assertThat(pedido.getQuantidade()).isEqualTo(4);
        assertThat(pedido.getValorUnitario()).isEqualByComparingTo("15.00");
        assertThat(pedido.getTotal()).isEqualByComparingTo("60.00");
        assertThat(pedido.getStatus()).isEqualTo("PAGO");
    }
}