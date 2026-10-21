package gabriel.tiziano.microservice_logistica.publisher;

import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogisticaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicoPreparando;

    public LogisticaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${order-management-microservices.kafka.topics.pedido-preparando-envio}") String topicoPreparando) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicoPreparando = topicoPreparando;
    }

    public void publicarPreparando(PedidoPreparandoEnvioRepresentation evento) {
        String chave = String.valueOf(evento.codigoPedido());
        kafkaTemplate.send(topicoPreparando, chave, evento).whenComplete((resultado, erro) -> {
            if (erro != null) {
                log.error("Falha ao publicar pedido-preparando-envio para o pedido {}: {}",
                        evento.codigoPedido(), erro.getMessage(), erro);
            } else {
                log.info("Evento pedido-preparando-envio publicado para o pedido {}", evento.codigoPedido());
            }
        });
    }
}