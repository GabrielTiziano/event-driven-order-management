package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoPreparandoEnvioConsumer {

    private final PedidoService pedidoService;

    public PedidoPreparandoEnvioConsumer(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-preparando-envio}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoPreparandoEnvioRepresentation evento) {
        log.info("Evento pedido-preparando-envio recebido para o pedido {}", evento.codigoPedido());
        pedidoService.confirmPreparingShipment(evento.codigoPedido());
    }
}