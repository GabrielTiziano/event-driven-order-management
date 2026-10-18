package gabriel.tiziano.microservice_faturamento.exception;

public abstract class FaturamentoException extends RuntimeException {

    protected FaturamentoException(String message) {
        super(message);
    }

    protected FaturamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}