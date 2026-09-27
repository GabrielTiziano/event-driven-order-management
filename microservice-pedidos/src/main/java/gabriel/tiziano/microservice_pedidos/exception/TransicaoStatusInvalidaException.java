package gabriel.tiziano.microservice_pedidos.exception;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(StatusPedido atual, StatusPedido destino) {
        super("Transição de status inválida: de " + atual + " para " + destino);
    }
}