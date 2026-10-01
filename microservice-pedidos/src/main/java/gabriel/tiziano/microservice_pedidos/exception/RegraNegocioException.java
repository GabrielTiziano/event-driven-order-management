package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public abstract class RegraNegocioException extends RuntimeException {
    private final HttpStatus status;

    protected RegraNegocioException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
