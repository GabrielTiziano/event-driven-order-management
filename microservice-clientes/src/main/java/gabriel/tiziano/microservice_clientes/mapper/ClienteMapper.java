package gabriel.tiziano.microservice_clientes.mapper;

import gabriel.tiziano.microservice_clientes.dto.ClienteRequest;
import gabriel.tiziano.microservice_clientes.dto.ClienteResponse;
import gabriel.tiziano.microservice_clientes.entity.Cliente;

public class ClienteMapper {
    private ClienteMapper() {}

    public static Cliente toEntity(ClienteRequest request) {
        return new Cliente(
                null,
                request.nome(),
                request.cpf(),
                request.logradouro(),
                request.numero(),
                request.bairro(),
                request.email(),
                request.telefone()
        );
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getCodigo(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getLogradouro(),
                cliente.getNumero(),
                cliente.getBairro(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }

    public static void updateEntity(Cliente cliente, ClienteRequest request) {
        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setLogradouro(request.logradouro());
        cliente.setNumero(request.numero());
        cliente.setBairro(request.bairro());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
    }
}
