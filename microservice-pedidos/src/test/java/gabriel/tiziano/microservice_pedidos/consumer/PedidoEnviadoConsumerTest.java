package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoEnviadoConsumerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoEnviadoConsumer consumer;

    @Test
    void deveDelegarEventoEnviadoParaOPedidoService() {
        consumer.consumir(new PedidoEnviadoRepresentation(1L, "LG123456789BR"));

        verify(pedidoService).confirmShipping(1L, "LG123456789BR");
    }
}
