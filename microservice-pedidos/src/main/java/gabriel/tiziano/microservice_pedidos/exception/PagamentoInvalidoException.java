package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class PagamentoInvalidoException extends RegraNegocioException {
    public PagamentoInvalidoException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}