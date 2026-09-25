package gabriel.tiziano.microservice_clientes.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long codigo) {
        super("Cliente com código " + codigo + " não encontrado.");
    }
}
