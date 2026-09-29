package gabriel.tiziano.microservice_pedidos.mapper;

import gabriel.tiziano.microservice_pedidos.dto.*;
import gabriel.tiziano.microservice_pedidos.entity.Endereco;
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
        pedido.setMetodoPagamento(request.metodoPagamento());
        pedido.setParcelas(request.parcelas());
        pedido.setEnderecoEntrega(toEndereco(request.enderecoEntrega()));
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.REALIZADO);

        request.itens().forEach(itemRequest -> pedido.addItem(toItemEntity(itemRequest)));
        pedido.setTotal(calcularTotal(pedido));

        return pedido;
    }

    private static Endereco toEndereco(EnderecoRequest request) {
        if (request == null) {
            return null;
        }
        return new Endereco(
                request.logradouro(),
                request.numero(),
                request.bairro(),
                request.cidade(),
                request.cep());
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
                pedido.getMetodoPagamento(),
                pedido.getParcelas(),
                pedido.getTotal(),
                pedido.getChavePagamento(),
                pedido.getObservacoes(),
                toEnderecoResponse(pedido.getEnderecoEntrega()),
                pedido.getCodigoRastreio(),
                pedido.getUrlNf(),
                itens);
    }

    private static EnderecoResponse toEnderecoResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoResponse(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getCep());
    }

    private static PedidoItemResponse toItemResponse(ItemPedido item) {
        return new PedidoItemResponse(
                item.getCodigo(),
                item.getCodigoProduto(),
                item.getQuantidade(),
                item.getValorUnitario());
    }
}