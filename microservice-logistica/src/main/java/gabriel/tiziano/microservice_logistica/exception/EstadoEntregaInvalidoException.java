package gabriel.tiziano.microservice_logistica.exception;

import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;

public class EstadoEntregaInvalidoException extends LogisticaException {
    public EstadoEntregaInvalidoException(Long codigoPedido, StatusEntrega statusAtual) {
        super("Entrega do pedido " + codigoPedido + " não pode ser despachada no status " + statusAtual);
    }
}