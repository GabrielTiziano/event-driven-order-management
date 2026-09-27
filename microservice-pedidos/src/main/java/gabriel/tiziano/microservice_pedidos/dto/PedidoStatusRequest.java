package gabriel.tiziano.microservice_pedidos.dto;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record PedidoStatusRequest(
        @NotNull(message = "O status é obrigatório")
        StatusPedido status
) {
}