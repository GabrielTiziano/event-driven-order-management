package gabriel.tiziano.microservice_faturamento.consumer;

import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.service.FaturamentoService;
import gabriel.tiziano.microservice_faturamento.service.GeradorNotaFiscalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PedidoPagoConsumer {

    private final FaturamentoService faturamentoService;

    public PedidoPagoConsumer(FaturamentoService faturamentoService) {
        this.faturamentoService = faturamentoService;
    }

    @KafkaListener(
            topics = "${order-management-microservices.kafka.topics.pedido-pago}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoPagoRepresentation pedidoPago) {
        log.info("Pedido pago recebido para faturamento: codigoPedido={}", pedidoPago.codigoPedido());
        faturamentoService.faturar(pedidoPago);
    }
}