package gabriel.tiziano.microservice_pedidos.dto;

public record EnderecoResponse(
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String cep
) {
}