package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.event.PedidoPagoEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PedidoPagoListener {

    private final PagamentoPublisher pagamentoPublisher;

    public PedidoPagoListener(PagamentoPublisher pagamentoPublisher) {
        this.pagamentoPublisher = pagamentoPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPedidoPago(PedidoPagoEvent event) {
        pagamentoPublisher.publicar(event.detalhePedidoRepresentation());
    }
}
