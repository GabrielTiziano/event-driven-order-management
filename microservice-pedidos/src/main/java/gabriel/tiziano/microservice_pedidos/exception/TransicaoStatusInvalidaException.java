package gabriel.tiziano.microservice_pedidos.exception;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import org.springframework.http.HttpStatus;

public class TransicaoStatusInvalidaException extends RegraNegocioException {
    public TransicaoStatusInvalidaException(StatusPedido atual, StatusPedido destino) {
        super("Transição de status inválida: de " + atual + " para " + destino, HttpStatus.CONFLICT);
    }
}