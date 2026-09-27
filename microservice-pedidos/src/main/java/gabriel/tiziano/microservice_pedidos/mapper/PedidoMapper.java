package gabriel.tiziano.microservice_pedidos.mapper;

import gabriel.tiziano.microservice_pedidos.dto.PedidoItemRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoItemResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoMapper {

    public static Pedido toEntity(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(request.codigoCliente());
        pedido.setObservacoes(request.observacoes());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.REALIZADO);

        request.itens().forEach(itemRequest -> pedido.addItem(toItemEntity(itemRequest)));
        pedido.setTotal(calcularTotal(pedido));

        return pedido;
    }

    private static ItemPedido toItemEntity(PedidoItemRequest request) {
        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(request.codigoProduto());
        item.setQuantidade(request.quantidade());
        item.setValorUnitario(request.valorUnitario());
        return item;
    }

    private static BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(item -> item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static PedidoResponse toResponse(Pedido pedido) {
        List<PedidoItemResponse> itens = pedido.getItens().stream()
                .map(PedidoMapper::toItemResponse)
                .toList();

        return new PedidoResponse(
                pedido.getCodigo(),
                pedido.getCodigoCliente(),
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getTotal(),
                pedido.getChavePagamento(),
                pedido.getObservacoes(),
                pedido.getCodigoRastreio(),
                pedido.getUrlNf(),
                itens
        );
    }

    private static PedidoItemResponse toItemResponse(ItemPedido item) {
        return new PedidoItemResponse(
                item.getCodigo(),
                item.getCodigoProduto(),
                item.getQuantidade(),
                item.getValorUnitario()
        );
    }
}