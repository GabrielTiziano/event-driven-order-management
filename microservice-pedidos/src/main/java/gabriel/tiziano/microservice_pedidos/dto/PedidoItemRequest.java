package gabriel.tiziano.microservice_pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PedidoItemRequest(
        @NotNull(message = "O código do produto é obrigatório")
        Long codigoProduto,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @NotNull(message = "O valor unitário é obrigatório")
        @PositiveOrZero(message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario
) {
}