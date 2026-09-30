package gabriel.tiziano.microservice_pedidos.validator;

import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.PagamentoInvalidoException;
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

    private Pedido pedidoComPagamento(MetodoPagamento metodo, Integer parcelas) {
        Pedido pedido = new Pedido();
        pedido.setMetodoPagamento(metodo);
        pedido.setParcelas(parcelas);
        return pedido;
    }

    // --- transição de status ---

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

    // --- pagamento ---

    @Test
    void credito_comParcelas_naoDeveLancar() {
        Pedido pedido = pedidoComPagamento(MetodoPagamento.CREDITO, 3);
        assertThatCode(() -> validator.validarPagamento(pedido))
                .doesNotThrowAnyException();
    }

    @Test
    void credito_semParcelas_deveLancar() {
        Pedido pedido = pedidoComPagamento(MetodoPagamento.CREDITO, null);
        assertThatThrownBy(() -> validator.validarPagamento(pedido))
                .isInstanceOf(PagamentoInvalidoException.class);
    }

    @Test
    void pix_semParcelas_naoDeveLancar() {
        Pedido pedido = pedidoComPagamento(MetodoPagamento.PIX, null);
        assertThatCode(() -> validator.validarPagamento(pedido))
                .doesNotThrowAnyException();
    }

    @Test
    void pix_comParcelas_deveLancar() {
        Pedido pedido = pedidoComPagamento(MetodoPagamento.PIX, 3);
        assertThatThrownBy(() -> validator.validarPagamento(pedido))
                .isInstanceOf(PagamentoInvalidoException.class);
    }
}