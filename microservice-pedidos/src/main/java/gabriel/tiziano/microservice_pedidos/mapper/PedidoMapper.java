package gabriel.tiziano.microservice_pedidos.mapper;

import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;

public class PedidoMapper {

    public static Pedido toEntity(PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(request.codigoCliente());
        pedido.setCodigoProduto(request.codigoProduto());
        pedido.setQuantidade(request.quantidade());
        pedido.setValorUnitario(request.valorUnitario());
        pedido.setTotal(request.total());
        pedido.setStatus(request.status());
        return pedido;
    }

    public static PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getCodigo(),
                pedido.getCodigoCliente(),
                pedido.getCodigoProduto(),
                pedido.getQuantidade(),
                pedido.getValorUnitario(),
                pedido.getTotal(),
                pedido.getStatus()
        );
    }

    public static void updateEntity(Pedido pedido, PedidoRequest request) {
        pedido.setCodigoCliente(request.codigoCliente());
        pedido.setCodigoProduto(request.codigoProduto());
        pedido.setQuantidade(request.quantidade());
        pedido.setValorUnitario(request.valorUnitario());
        pedido.setTotal(request.total());
        pedido.setStatus(request.status());
    }
}