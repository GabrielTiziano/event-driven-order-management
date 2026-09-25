package gabriel.tiziano.microservice_clientes.service;

import gabriel.tiziano.microservice_clientes.dto.ClienteRequest;
import gabriel.tiziano.microservice_clientes.dto.ClienteResponse;
import gabriel.tiziano.microservice_clientes.entity.Cliente;
import gabriel.tiziano.microservice_clientes.exception.ClienteNotFoundException;
import gabriel.tiziano.microservice_clientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    // helper pra reduzir repetição na criação de clientes de teste
    private Cliente cliente(Long codigo, String nome) {
        return new Cliente(codigo, nome, "12345678901", "Rua A", "10", "Centro", "a@b.com", "4199");
    }

    @Test
    void findClientById_deveRetornarClienteQuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente(1L, "Maria")));

        ClienteResponse response = service.findClientById(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Maria");
        verify(repository).findById(1L);
    }

    @Test
    void findClientById_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findClientById(99L))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createClient_deveSalvarERetornarCliente() {
        ClienteRequest request = new ClienteRequest("Maria", "12345678901",
                "Rua A", "10", "Centro", "a@b.com", "4199");
        when(repository.save(any(Cliente.class))).thenReturn(cliente(1L, "Maria"));

        ClienteResponse response = service.createClient(request);

        assertThat(response.codigo()).isEqualTo(1L);
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void findAllClients_deveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(cliente(1L, "A"), cliente(2L, "B")));

        List<ClienteResponse> lista = service.findAllClients();

        assertThat(lista).hasSize(2)
                .extracting(ClienteResponse::nome).containsExactly("A", "B");
    }

    @Test
    void findAllClients_deveRetornarListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findAllClients()).isEmpty();
    }

    @Test
    void updateClient_deveAtualizarQuandoExiste() {
        Cliente existente = cliente(1L, "Antigo");
        ClienteRequest request = new ClienteRequest("Novo", "22222222222",
                "Rua nova", "999", "Novo", "new@b.com", "4188");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteResponse response = service.updateClient(1L, request);

        assertThat(response.nome()).isEqualTo("Novo");
        assertThat(response.cpf()).isEqualTo("22222222222");
        verify(repository).save(existente);
    }

    @Test
    void updateClient_deveLancarExcecaoQuandoNaoExiste() {
        ClienteRequest request = new ClienteRequest("X", "12345678901",
                null, null, null, null, null);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateClient(99L, request))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteClient_deveDeletarQuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteClient(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteClient_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteClient(99L))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(repository, never()).deleteById(anyLong());
    }
}