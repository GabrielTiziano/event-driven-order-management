package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.entity.DadosCliente;
import gabriel.tiziano.microservice_pedidos.entity.Endereco;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalheItemPedidoRepresentation;
import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;

import java.util.List;

public class DetalhePedidoMapper {

    public static DetalhePedidoRepresentation toDetalhePedido(Pedido pedido) {
        List<DetalheItemPedidoRepresentation> itens = pedido.getItens().stream()
                .map(DetalhePedidoMapper::toDetalheItem)
                .toList();

        DadosCliente cliente = pedido.getDadosCliente();
        Endereco endereco = pedido.getEnderecoEntrega();

        return new DetalhePedidoRepresentation(
                pedido.getCodigo(),
                pedido.getCodigoCliente(),
                cliente != null ? cliente.getNome() : null,
                cliente != null ? cliente.getCpf() : null,
                endereco != null ? endereco.getLogradouro() : null,
                endereco != null ? endereco.getNumero() : null,
                endereco != null ? endereco.getBairro() : null,
                cliente != null ? cliente.getEmail() : null,
                cliente != null ? cliente.getTelefone() : null,
                pedido.getDataPedido() != null ? pedido.getDataPedido().toString() : null,
                pedido.getTotal(),
                pedido.getStatus(),
                itens);
    }

    private static DetalheItemPedidoRepresentation toDetalheItem(ItemPedido item) {
        return new DetalheItemPedidoRepresentation(
                item.getCodigoProduto(),
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getValorUnitario());
    }
}