package gabriel.tiziano.microservice_pedidos.validator;

import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();

    private Pedido pedidoComStatus(StatusPedido status) {
        Pedido pedido = new Pedido();
        pedido.setStatus(status);
        return pedido;
    }

    @Test
    void transicaoValida_naoDeveLancar() {
        Pedido pedido = pedidoComStatus(StatusPedido.REALIZADO);
        assertThatCode(() -> validator.validarTransicaoStatus(pedido, StatusPedido.PAGO))
                .doesNotThrowAnyException();
    }

    @Test
    void retentativaDePagamento_naoDeveLancar() {
        Pedido pedido = pedidoComStatus(StatusPedido.ERRO_PAGAMENTO);
        assertThatCode(() -> validator.validarTransicaoStatus(pedido, StatusPedido.PAGO))
                .doesNotThrowAnyException();
    }

    @Test
    void transicaoInvalida_deveLancar() {
        Pedido pedido = pedidoComStatus(StatusPedido.REALIZADO);
        assertThatThrownBy(() -> validator.validarTransicaoStatus(pedido, StatusPedido.ENVIADO))
                .isInstanceOf(TransicaoStatusInvalidaException.class)
                .hasMessageContaining("REALIZADO")
                .hasMessageContaining("ENVIADO");
    }
}