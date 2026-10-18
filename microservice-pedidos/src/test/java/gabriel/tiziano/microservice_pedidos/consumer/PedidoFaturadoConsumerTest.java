package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoFaturadoRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoFaturadoConsumerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoFaturadoConsumer pedidoFaturadoConsumer;

    @Test
    void deveDelegarEventoFaturadoParaOPedidoService() {
        PedidoFaturadoRepresentation evento =
                new PedidoFaturadoRepresentation(1L, "nota-fiscal-1.pdf");

        pedidoFaturadoConsumer.consumir(evento);

        verify(pedidoService).markOrderAsInvoiced(1L, "nota-fiscal-1.pdf");
    }
}