package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoEnviadoConsumer {
    private final PedidoService pedidoService;

    public PedidoEnviadoConsumer(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-enviado}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoEnviadoRepresentation evento) {
        log.info("Evento pedido-enviado recebido para o pedido {} (rastreio={})",
                evento.codigoPedido(), evento.codigoRastreio());
        pedidoService.confirmShipping(evento.codigoPedido(), evento.codigoRastreio());
    }
}
