package gabriel.tiziano.microservice_pedidos.dto;

import java.math.BigDecimal;

public record PedidoResponse(
        Long codigo,
        Long codigoCliente,
        Long codigoProduto,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal total,
        String status
) {
}