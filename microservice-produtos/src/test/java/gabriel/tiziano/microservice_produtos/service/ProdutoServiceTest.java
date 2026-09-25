package gabriel.tiziano.microservice_produtos.service;

import gabriel.tiziano.microservice_produtos.dto.ProdutoRequest;
import gabriel.tiziano.microservice_produtos.dto.ProdutoResponse;
import gabriel.tiziano.microservice_produtos.entity.Produto;
import gabriel.tiziano.microservice_produtos.exception.ProdutoNotFoundException;
import gabriel.tiziano.microservice_produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    void findProductById_deveRetornarProdutoQuandoExiste() {
        Produto produto = new Produto(1L, "Notebook", "Dell", new BigDecimal("4999.90"), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponse response = service.findProductById(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Notebook");
        verify(repository).findById(1L);
    }

    @Test
    void findProductById_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findProductById(99L))
                .isInstanceOf(ProdutoNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository).findById(99L);
    }

    @Test
    void createProduct_deveSalvarERetornarProduto() {
        ProdutoRequest request = new ProdutoRequest("Mouse", "Sem fio", new BigDecimal("120.00"), 50);
        Produto salvo = new Produto(1L, "Mouse", "Sem fio", new BigDecimal("120.00"), 50);
        when(repository.save(any(Produto.class))).thenReturn(salvo);

        ProdutoResponse response = service.createProduct(request);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Mouse");
        verify(repository).save(any(Produto.class));
    }

    @Test
    void findAllProducts_deveRetornarListaDeProdutos() {
        when(repository.findAll()).thenReturn(List.of(
                new Produto(1L, "A", "descA", new BigDecimal("10.00"), 1),
                new Produto(2L, "B", "descB", new BigDecimal("20.00"), 2)
        ));

        List<ProdutoResponse> lista = service.findAllProducts();

        assertThat(lista).hasSize(2)
                .extracting(ProdutoResponse::nome).containsExactly("A", "B");
    }

    @Test
    void findAllProducts_deveRetornarListaVaziaQuandoNaoHaProdutos() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findAllProducts()).isEmpty();
    }

    @Test
    void updateProduct_deveAtualizarQuandoExiste() {
        Produto existente = new Produto(1L, "Antigo", "desc", new BigDecimal("10.00"), 5);
        ProdutoRequest request = new ProdutoRequest("Novo", "nova desc", new BigDecimal("99.90"), 20);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = service.updateProduct(1L, request);

        assertThat(response.nome()).isEqualTo("Novo");
        assertThat(response.preco()).isEqualByComparingTo("99.90");
        verify(repository).save(existente);
    }

    @Test
    void updateProduct_deveLancarExcecaoQuandoNaoExiste() {
        ProdutoRequest request = new ProdutoRequest("X", "y", new BigDecimal("1.00"), 1);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProduct(99L, request))
                .isInstanceOf(ProdutoNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteProduct_deveDeletarQuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteProduct(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteProduct_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteProduct(99L))
                .isInstanceOf(ProdutoNotFoundException.class);

        verify(repository, never()).deleteById(anyLong());
    }
}