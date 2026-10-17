package gabriel.tiziano.microservice_faturamento.consumer;

import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoPagoConsumer {

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-pago}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoPagoRepresentation pedidoPago) {
        log.info("Pedido pago recebido para faturamento: codigoPedido={}", pedidoPago.codigoPedido());

        // próximo passo: gerar a NF (PDF) -> BucketService.upload -> persistir entidade NotaFiscal com a key
    }
}