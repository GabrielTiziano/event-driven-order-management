package gabriel.tiziano.microservice_pedidos.exception;

public class PagamentoInvalidoException extends RuntimeException {
    public PagamentoInvalidoException(String message) {
        super(message);
    }
}