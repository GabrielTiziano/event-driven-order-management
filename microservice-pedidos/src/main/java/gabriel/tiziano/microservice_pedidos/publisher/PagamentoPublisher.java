package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PagamentoPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${order-management-microservices.kafka.topics.pedido-pago}")
    private String topico;

    public PagamentoPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(DetalhePedidoRepresentation evento) {
        log.info("Publicando pedido pago {}", evento.codigoPedido());

        kafkaTemplate.send(topico, String.valueOf(evento.codigoPedido()), evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar pedido pago {}", evento.codigoPedido(), ex);
                    }
                });
    }
}