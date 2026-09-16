package gabriel.tiziano.microservice_produtos.dto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long codigo,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidade
) {
}
