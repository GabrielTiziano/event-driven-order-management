package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class PedidoNotFoundException extends RegraNegocioException {
    public PedidoNotFoundException(Long codigo) {
        super("Pedido não encontrado com o código: " + codigo, HttpStatus.NOT_FOUND);
    }
}