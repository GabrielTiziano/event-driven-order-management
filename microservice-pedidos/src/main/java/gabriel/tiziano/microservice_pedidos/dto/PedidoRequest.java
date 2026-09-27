package gabriel.tiziano.microservice_pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PedidoRequest(
        @NotNull(message = "O código do cliente é obrigatório")
        Long codigoCliente,

        @NotNull(message = "O código do produto é obrigatório")
        Long codigoProduto,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @NotNull(message = "O valor unitário é obrigatório")
        @PositiveOrZero(message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @NotNull(message = "O total é obrigatório")
        @PositiveOrZero(message = "O total não pode ser negativo")
        BigDecimal total,

        @NotBlank(message = "O status é obrigatório")
        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status
) {
}