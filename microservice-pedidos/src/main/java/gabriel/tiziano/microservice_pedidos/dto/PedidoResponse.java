package gabriel.tiziano.microservice_pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long codigo,
        Long codigoCliente,
        LocalDateTime dataPedido,
        String status,
        BigDecimal total,
        String chavePagamento,
        String observacoes,
        String codigoRastreio,
        String urlNf,
        List<PedidoItemResponse> itens
) {
}