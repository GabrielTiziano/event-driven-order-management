package gabriel.tiziano.microservice_pedidos.dto;

import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import jakarta.validation.constraints.NotNull;

public record RetryPagamentoRequest(
        @NotNull MetodoPagamento metodoPagamento,
        Integer parcelas
) {
}
