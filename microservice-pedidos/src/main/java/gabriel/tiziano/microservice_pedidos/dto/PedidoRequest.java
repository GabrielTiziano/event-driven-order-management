package gabriel.tiziano.microservice_pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
        @NotNull(message = "O código do cliente é obrigatório")
        Long codigoCliente,

        String observacoes,

        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<@Valid PedidoItemRequest> itens
) {
}