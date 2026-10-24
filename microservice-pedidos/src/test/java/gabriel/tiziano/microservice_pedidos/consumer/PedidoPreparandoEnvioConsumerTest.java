package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoPreparandoEnvioConsumerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoPreparandoEnvioConsumer consumer;

    @Test
    void deveDelegarEventoPreparandoEnvioParaOPedidoService() {
        consumer.consumir(new PedidoPreparandoEnvioRepresentation(1L));

        verify(pedidoService).confirmPreparingShipment(1L);
    }
}
