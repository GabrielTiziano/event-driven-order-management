package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class WebhookNaoAutorizadoException extends RegraNegocioException {
    public WebhookNaoAutorizadoException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
