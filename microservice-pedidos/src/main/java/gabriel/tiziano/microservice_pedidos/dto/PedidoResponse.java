package gabriel.tiziano.microservice_pedidos.dto;

import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long codigo,
        Long codigoCliente,
        LocalDateTime dataPedido,
        StatusPedido status,
        MetodoPagamento metodoPagamento,
        Integer parcelas,
        BigDecimal total,
        String chavePagamento,
        String observacoes,
        EnderecoResponse enderecoEntrega,
        String codigoRastreio,
        String urlNf,
        List<PedidoItemResponse> itens
) {
}