package gabriel.tiziano.microservice_logistica.exception;

public class EntregaNaoEncontradaException extends LogisticaException {
    public EntregaNaoEncontradaException(Long codigoPedido) {
        super("Entrega não encontrada para o pedido " + codigoPedido);
    }
}