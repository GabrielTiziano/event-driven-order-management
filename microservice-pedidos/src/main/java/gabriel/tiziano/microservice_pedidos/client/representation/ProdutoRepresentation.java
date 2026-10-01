package gabriel.tiziano.microservice_pedidos.client.representation;

import java.math.BigDecimal;

public record ProdutoRepresentation(
        Long codigo,
        BigDecimal preco,
        Integer quantidade
) {
}
