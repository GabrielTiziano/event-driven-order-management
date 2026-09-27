package gabriel.tiziano.microservice_pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PedidoStatusRequest(
        @NotBlank(message = "O status é obrigatório")
        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status
) {
}