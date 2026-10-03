package gabriel.tiziano.microservice_pedidos.dto;

public record CallbackPagamentoRequest(
        Long codigo,
        String chavePagamento,
        boolean aprovado,
        String observacoes
) {
}
