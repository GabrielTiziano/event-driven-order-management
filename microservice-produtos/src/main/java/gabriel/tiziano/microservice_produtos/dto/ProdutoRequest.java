package gabriel.tiziano.microservice_produtos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "A quantidade é obrigatória")
        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantidade
) {
}
