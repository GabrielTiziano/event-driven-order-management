package gabriel.tiziano.microservice_pedidos.dto;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long codigo,
        Long codigoCliente,
        LocalDateTime dataPedido,
        StatusPedido status,
        BigDecimal total,
        String chavePagamento,
        String observacoes,
        String codigoRastreio,
        String urlNf,
        List<PedidoItemResponse> itens
) {
}