package gabriel.tiziano.microservice_pedidos.consumer;

import gabriel.tiziano.microservice_pedidos.consumer.representation.PedidoFaturadoRepresentation;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoFaturadoConsumer {
    private final PedidoService pedidoService;

    public PedidoFaturadoConsumer(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-faturado}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoFaturadoRepresentation evento) {
        log.info("Pedido faturado recebido: codigoPedido={}", evento.codigoPedido());
        pedidoService.markOrderAsInvoiced(evento.codigoPedido(), evento.chaveArquivo());
    }
}
