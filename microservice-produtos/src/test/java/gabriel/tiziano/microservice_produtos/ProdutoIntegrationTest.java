package gabriel.tiziano.microservice_produtos;

import gabriel.tiziano.microservice_produtos.entity.Produto;
import gabriel.tiziano.microservice_produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto persistirProduto() {
        return produtoRepository.save(
                new Produto(null, "Teclado", "Mecânico", new BigDecimal("350.00"), 15));
    }

    @Test
    void create_devePersistirProdutoERetornar201() throws Exception {
        long antes = produtoRepository.count();

        String json = """
                { "nome": "Teclado", "descricao": "Mecânico", "preco": 350.00, "quantidade": 15 }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Teclado"));

        assertThat(produtoRepository.count()).isEqualTo(antes + 1);
    }

    @Test
    void create_comDadosInvalidos_deveRetornar400() throws Exception {
        String json = """
                { "nome": "", "preco": -5.00, "quantidade": 10 }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findById_quandoExiste_deveRetornar200() throws Exception {
        Produto salvo = persistirProduto();

        mockMvc.perform(get("/produtos/{codigo}", salvo.getCodigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(salvo.getCodigo()))
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    void findById_quandoNaoExiste_deveRetornar404() throws Exception {
        mockMvc.perform(get("/produtos/{codigo}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_devePersistirAlteracoes() throws Exception {
        Produto salvo = persistirProduto();

        String json = """
                { "nome": "Teclado RGB", "descricao": "Mecânico RGB", "preco": 420.00, "quantidade": 8 }
                """;

        mockMvc.perform(put("/produtos/{codigo}", salvo.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Teclado RGB"));

        Produto atualizado = produtoRepository.findById(salvo.getCodigo()).orElseThrow();
        assertThat(atualizado.getNome()).isEqualTo("Teclado RGB");
        assertThat(atualizado.getPreco()).isEqualByComparingTo("420.00");
    }

    @Test
    void delete_deveRemoverProduto() throws Exception {
        Produto salvo = persistirProduto();

        mockMvc.perform(delete("/produtos/{codigo}", salvo.getCodigo()))
                .andExpect(status().isNoContent());

        assertThat(produtoRepository.findById(salvo.getCodigo())).isEmpty();
    }
}