package gabriel.tiziano.microservice_pedidos.dto;

import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequest(
        @NotNull(message = "O código do cliente é obrigatório")
        Long codigoCliente,

        String observacoes,

        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodoPagamento,

        @Positive(message = "O número de parcelas deve ser maior que zero")
        Integer parcelas,

        @Valid
        EnderecoRequest enderecoEntrega,

        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<@Valid PedidoItemRequest> itens
) {
}