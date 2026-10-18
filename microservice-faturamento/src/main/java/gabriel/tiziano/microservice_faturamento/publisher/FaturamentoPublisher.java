package gabriel.tiziano.microservice_faturamento.publisher;

import gabriel.tiziano.microservice_faturamento.publisher.representation.PedidoFaturadoRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaturamentoPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topico;

    public FaturamentoPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${order-management-microservices.kafka.topics.pedido-faturado}") String topico) {
        this.kafkaTemplate = kafkaTemplate;
        this.topico = topico;
    }

    public void publicar(PedidoFaturadoRepresentation evento) {
        String codigoPedido = String.valueOf(evento.codigoPedido());
        kafkaTemplate.send(topico, codigoPedido, evento).whenComplete((resultado, erro) -> {
            if (erro != null) {
                log.error("Falha ao publicar pedido-faturado para o pedido {}: {}",
                        evento.codigoPedido(), erro.getMessage(), erro);
            } else {
                log.info("Evento pedido-faturado publicado para o pedido {}", evento.codigoPedido());
            }
        });
    }
}