package gabriel.tiziano.microservice_logistica.publisher;

import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogisticaPublisherTest {

    private static final String TOPICO_PREPARANDO = "pedido-preparando-envio";
    private static final String TOPICO_ENVIADO = "pedido-enviado";

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

    private final LogisticaPublisher publisher =
            new LogisticaPublisher(kafkaTemplate, TOPICO_PREPARANDO, TOPICO_ENVIADO);

    @Test
    void publicarPreparandoDevePublicarNoTopicoCorretoComAChaveSendoOCodigoPedido() {
        PedidoPreparandoEnvioRepresentation evento = new PedidoPreparandoEnvioRepresentation(1L);
        when(kafkaTemplate.send(eq(TOPICO_PREPARANDO), eq("1"), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publicarPreparando(evento);

        verify(kafkaTemplate).send(eq(TOPICO_PREPARANDO), eq("1"), eq(evento));
    }

    @Test
    void publicarEnviadoDevePublicarNoTopicoCorretoComAChaveSendoOCodigoPedido() {
        PedidoEnviadoRepresentation evento = new PedidoEnviadoRepresentation(1L, "LG123456789BR");
        when(kafkaTemplate.send(eq(TOPICO_ENVIADO), eq("1"), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publicarEnviado(evento);

        verify(kafkaTemplate).send(eq(TOPICO_ENVIADO), eq("1"), eq(evento));
    }
}