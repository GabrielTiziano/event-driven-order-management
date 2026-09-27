package gabriel.tiziano.microservice_pedidos.dto;

import java.math.BigDecimal;

public record PedidoItemResponse(
        Long codigo,
        Long codigoProduto,
        Integer quantidade,
        BigDecimal valorUnitario
) {
}