package gabriel.tiziano.microservice_pedidos.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long codigo) {
        super("Pedido de código " + codigo + "não encontrado.");
    }
}