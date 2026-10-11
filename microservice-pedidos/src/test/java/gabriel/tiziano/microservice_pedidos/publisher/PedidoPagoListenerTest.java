package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.event.PedidoPagoEvent;
import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoPagoListenerTest {

    @Mock
    private PagamentoPublisher pagamentoPublisher;

    @InjectMocks
    private PedidoPagoListener listener;

    @Test
    void onPedidoPago_deveDelegarParaOPublisher() {
        DetalhePedidoRepresentation detalhe = new DetalhePedidoRepresentation(
                1L, 2L, "Fulano", "12345678901", "Rua A", "100", "Centro",
                "fulano@email.com", "41999999999", "2026-01-01T10:00",
                new BigDecimal("20.00"), StatusPedido.PAGO, List.of());

        listener.onPedidoPago(new PedidoPagoEvent(detalhe));

        verify(pagamentoPublisher).publicar(detalhe);
    }
}