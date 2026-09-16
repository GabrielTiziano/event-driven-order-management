package gabriel.tiziano.microservice_produtos.mapper;

import gabriel.tiziano.microservice_produtos.dto.ProdutoRequest;
import gabriel.tiziano.microservice_produtos.dto.ProdutoResponse;
import gabriel.tiziano.microservice_produtos.entity.Produto;

public class ProdutoMapper {
    private ProdutoMapper() {}

    public static Produto toEntity(ProdutoRequest request) {
        return new Produto(
                null,
                request.nome(),
                request.descricao(),
                request.preco(),
                request.quantidade()
        );
    }

    public static ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade()
        );
    }
}
