package gabriel.tiziano.microservice_logistica.exception;

public abstract class LogisticaException extends RuntimeException {
    protected LogisticaException(String message) {
        super(message);
    }
    protected LogisticaException(String message, Throwable cause) {
        super(message, cause);
    }
}