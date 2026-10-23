package gabriel.tiziano.microservice_logistica.publisher;

import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoEnviadoRepresentation;
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
    private final String topicoEnviado;

    public LogisticaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${order-management-microservices.kafka.topics.pedido-preparando-envio}") String topicoPreparando,
                              @Value("${order-management-microservices.kafka.topics.pedido-enviado}") String topicoEnviado) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicoPreparando = topicoPreparando;
        this.topicoEnviado = topicoEnviado;
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

    public void publicarEnviado(PedidoEnviadoRepresentation evento) {
        enviar(topicoEnviado, String.valueOf(evento.codigoPedido()), evento, "pedido-enviado", evento.codigoPedido());
    }

    public void enviar(String topico, String chave, Object evento, String nomeEvento, Long codigoPedido) {
        kafkaTemplate.send(topico, chave, evento).whenComplete((resultado, erro) -> {
            if (erro != null) {
                log.error("Falha ao publicar {} para o pedido {}: {}", nomeEvento, codigoPedido, erro.getMessage(), erro);
            } else {
                log.info("Evento {} publicado para o pedido {}", nomeEvento, codigoPedido);
            }
        });
    }
}