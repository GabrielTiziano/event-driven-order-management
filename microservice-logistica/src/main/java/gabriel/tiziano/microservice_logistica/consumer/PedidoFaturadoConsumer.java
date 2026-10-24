package gabriel.tiziano.microservice_logistica.consumer;

import gabriel.tiziano.microservice_logistica.consumer.representation.PedidoFaturadoRepresentation;
import gabriel.tiziano.microservice_logistica.service.LogisticaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoFaturadoConsumer {

    private final LogisticaService logisticaService;

    public PedidoFaturadoConsumer(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-faturado}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoFaturadoRepresentation evento) {
        log.info("Pedido faturado recebido na logística: codigoPedido={}", evento.codigoPedido());
        logisticaService.receberParaPreparacao(evento.codigoPedido());
    }
}