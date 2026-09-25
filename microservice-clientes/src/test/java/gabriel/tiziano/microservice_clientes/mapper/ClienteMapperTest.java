package gabriel.tiziano.microservice_clientes.mapper;

import gabriel.tiziano.microservice_clientes.dto.ClienteRequest;
import gabriel.tiziano.microservice_clientes.dto.ClienteResponse;
import gabriel.tiziano.microservice_clientes.entity.Cliente;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteMapperTest {

    @Test
    void toEntity_deveConverterRequestEmEntity() {
        ClienteRequest request = new ClienteRequest("Maria", "12345678901",
                "Rua A", "100", "Centro", "maria@email.com", "41999999999");

        Cliente cliente = ClienteMapper.toEntity(request);

        assertThat(cliente.getCodigo()).isNull();
        assertThat(cliente.getNome()).isEqualTo("Maria");
        assertThat(cliente.getCpf()).isEqualTo("12345678901");
        assertThat(cliente.getLogradouro()).isEqualTo("Rua A");
        assertThat(cliente.getNumero()).isEqualTo("100");
        assertThat(cliente.getBairro()).isEqualTo("Centro");
        assertThat(cliente.getEmail()).isEqualTo("maria@email.com");
        assertThat(cliente.getTelefone()).isEqualTo("41999999999");
    }

    @Test
    void toResponse_deveConverterEntityEmResponse() {
        Cliente cliente = new Cliente(1L, "João", "98765432100",
                "Av B", "200", "Bairro X", "joao@email.com", "4188888888");

        ClienteResponse response = ClienteMapper.toResponse(cliente);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João");
        assertThat(response.cpf()).isEqualTo("98765432100");
        assertThat(response.email()).isEqualTo("joao@email.com");
    }

    @Test
    void updateEntity_deveAtualizarCamposPreservandoCodigo() {
        Cliente cliente = new Cliente(1L, "Antigo", "11111111111",
                "Rua velha", "1", "Velho", "old@email.com", "410000");
        ClienteRequest request = new ClienteRequest("Novo", "22222222222",
                "Rua nova", "999", "Novo", "new@email.com", "419999");

        ClienteMapper.updateEntity(cliente, request);

        assertThat(cliente.getCodigo()).isEqualTo(1L);
        assertThat(cliente.getNome()).isEqualTo("Novo");
        assertThat(cliente.getCpf()).isEqualTo("22222222222");
        assertThat(cliente.getLogradouro()).isEqualTo("Rua nova");
        assertThat(cliente.getEmail()).isEqualTo("new@email.com");
    }
}