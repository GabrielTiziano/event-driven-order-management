package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PagamentoPublisher publisher;

    @Test
    void publicar_deveEnviarEventoComCodigoDoPedidoComoChave() {
        ReflectionTestUtils.setField(publisher, "topico", "pedidos-pagos");

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        DetalhePedidoRepresentation detalhe = new DetalhePedidoRepresentation(
                1L, 2L, "Fulano", "12345678901", "Rua A", "100", "Centro",
                "fulano@email.com", "41999999999", "2026-01-01T10:00",
                new BigDecimal("20.00"), StatusPedido.PAGO, List.of());

        publisher.publicar(detalhe);

        verify(kafkaTemplate).send("pedidos-pagos", "1", detalhe);
    }
}