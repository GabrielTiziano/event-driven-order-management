package gabriel.tiziano.microservice_pedidos.client.representation;

public record ClienteRepresentation(
        Long codigo,
        String nome,
        String cpf,
        String email,
        String telefone
) {
}
