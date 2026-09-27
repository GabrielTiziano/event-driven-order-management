package gabriel.tiziano.microservice_clientes;

import gabriel.tiziano.microservice_clientes.entity.Cliente;
import gabriel.tiziano.microservice_clientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente persistirCliente() {
        return clienteRepository.save(new Cliente(
                null, "Maria Silva", "12345678901",
                "Rua A", "100", "Centro", "maria@email.com", "41999999999"));
    }

    @Test
    void create_devePersistirClienteERetornar201() throws Exception {
        long antes = clienteRepository.count();

        String json = """
                {
                  "nome": "Maria Silva",
                  "cpf": "12345678901",
                  "logradouro": "Rua A",
                  "numero": "100",
                  "bairro": "Centro",
                  "email": "maria@email.com",
                  "telefone": "41999999999"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Maria Silva"));

        assertThat(clienteRepository.count()).isEqualTo(antes + 1);
    }

    @Test
    void create_comCpfInvalido_deveRetornar400() throws Exception {
        String json = """
                { "nome": "Maria Silva", "cpf": "123" }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findById_quandoExiste_deveRetornar200() throws Exception {
        Cliente salvo = persistirCliente();

        mockMvc.perform(get("/clientes/{codigo}", salvo.getCodigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(salvo.getCodigo()))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    void findById_quandoNaoExiste_deveRetornar404() throws Exception {
        mockMvc.perform(get("/clientes/{codigo}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_devePersistirAlteracoes() throws Exception {
        Cliente salvo = persistirCliente();

        String json = """
                {
                  "nome": "Maria Souza",
                  "cpf": "12345678901",
                  "logradouro": "Rua B",
                  "numero": "200",
                  "bairro": "Batel",
                  "email": "souza@email.com",
                  "telefone": "41988888888"
                }
                """;

        mockMvc.perform(put("/clientes/{codigo}", salvo.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Souza"));

        Cliente atualizado = clienteRepository.findById(salvo.getCodigo()).orElseThrow();
        assertThat(atualizado.getNome()).isEqualTo("Maria Souza");
        assertThat(atualizado.getBairro()).isEqualTo("Batel");
    }

    @Test
    void delete_deveRemoverCliente() throws Exception {
        Cliente salvo = persistirCliente();

        mockMvc.perform(delete("/clientes/{codigo}", salvo.getCodigo()))
                .andExpect(status().isNoContent());

        assertThat(clienteRepository.findById(salvo.getCodigo())).isEmpty();
    }
}