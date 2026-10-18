package gabriel.tiziano.microservice_faturamento.publisher;

import gabriel.tiziano.microservice_faturamento.publisher.representation.PedidoFaturadoRepresentation;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FaturamentoPublisherTest {

    private static final String TOPICO = "pedido-faturado";

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

    private final FaturamentoPublisher publisher = new FaturamentoPublisher(kafkaTemplate, TOPICO);

    @Test
    void devePublicarNoTopicoComAChaveSendoOCodigoPedido() {
        PedidoFaturadoRepresentation evento = new PedidoFaturadoRepresentation(100L, "nota-fiscal-100.pdf");
        when(kafkaTemplate.send(eq(TOPICO), eq("100"), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publicar(evento);

        verify(kafkaTemplate).send(eq(TOPICO), eq("100"), eq(evento));
    }
}