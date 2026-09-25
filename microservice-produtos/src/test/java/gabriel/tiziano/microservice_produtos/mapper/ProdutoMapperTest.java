package gabriel.tiziano.microservice_produtos.mapper;

import gabriel.tiziano.microservice_produtos.dto.ProdutoRequest;
import gabriel.tiziano.microservice_produtos.dto.ProdutoResponse;
import gabriel.tiziano.microservice_produtos.entity.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoMapperTest {

    @Test
    void toEntity_deveConverterRequestEmEntity() {
        ProdutoRequest request = new ProdutoRequest("Notebook", "Dell i7", new BigDecimal("4999.90"), 10);

        Produto produto = ProdutoMapper.toEntity(request);

        assertThat(produto.getCodigo()).isNull();            // o banco gera o id
        assertThat(produto.getNome()).isEqualTo("Notebook");
        assertThat(produto.getDescricao()).isEqualTo("Dell i7");
        assertThat(produto.getPreco()).isEqualByComparingTo("4999.90");
        assertThat(produto.getQuantidade()).isEqualTo(10);
    }

    @Test
    void toResponse_deveConverterEntityEmResponse() {
        Produto produto = new Produto(1L, "Mouse", "Sem fio", new BigDecimal("120.00"), 50);

        ProdutoResponse response = ProdutoMapper.toResponse(produto);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Mouse");
        assertThat(response.descricao()).isEqualTo("Sem fio");
        assertThat(response.preco()).isEqualByComparingTo("120.00");
        assertThat(response.quantidade()).isEqualTo(50);
    }

    @Test
    void updateEntity_deveAtualizarCamposPreservandoCodigo() {
        Produto produto = new Produto(1L, "Antigo", "desc antiga", new BigDecimal("10.00"), 5);
        ProdutoRequest request = new ProdutoRequest("Novo", "desc nova", new BigDecimal("99.90"), 20);

        ProdutoMapper.updateEntity(produto, request);

        assertThat(produto.getCodigo()).isEqualTo(1L);
        assertThat(produto.getNome()).isEqualTo("Novo");
        assertThat(produto.getDescricao()).isEqualTo("desc nova");
        assertThat(produto.getPreco()).isEqualByComparingTo("99.90");
        assertThat(produto.getQuantidade()).isEqualTo(20);
    }
}