package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class ClienteNotFoundException extends RegraNegocioException {
    public ClienteNotFoundException(Long codigo) {
        super("Cliente não encontrado com o código: " + codigo, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}