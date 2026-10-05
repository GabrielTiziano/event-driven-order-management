package gabriel.tiziano.microservice_pedidos.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusPedidoTest {

    @Test
    void transicoesValidas_devemSerPermitidas() {
        assertThat(StatusPedido.REALIZADO.podeTransicionarPara(StatusPedido.PAGO)).isTrue();
        assertThat(StatusPedido.REALIZADO.podeTransicionarPara(StatusPedido.ERRO_PAGAMENTO)).isTrue();
        assertThat(StatusPedido.PAGO.podeTransicionarPara(StatusPedido.FATURADO)).isTrue();
        assertThat(StatusPedido.FATURADO.podeTransicionarPara(StatusPedido.PREPARANDO_ENVIO)).isTrue();
        assertThat(StatusPedido.PREPARANDO_ENVIO.podeTransicionarPara(StatusPedido.ENVIADO)).isTrue();
    }

    @Test
    void transicoesInvalidas_naoDevemSerPermitidas() {
        assertThat(StatusPedido.REALIZADO.podeTransicionarPara(StatusPedido.FATURADO)).isFalse();
        assertThat(StatusPedido.PAGO.podeTransicionarPara(StatusPedido.REALIZADO)).isFalse();
        assertThat(StatusPedido.ENVIADO.podeTransicionarPara(StatusPedido.PAGO)).isFalse();
    }

    @Test
    void erroPagamento_devePermitirRetentativa() {
        assertThat(StatusPedido.ERRO_PAGAMENTO.podeTransicionarPara(StatusPedido.REALIZADO)).isTrue();
    }

    @Test
    void enviado_deveSerEstadoFinal() {
        assertThat(StatusPedido.ENVIADO.isFinal()).isTrue();
        assertThat(StatusPedido.REALIZADO.isFinal()).isFalse();
    }
}