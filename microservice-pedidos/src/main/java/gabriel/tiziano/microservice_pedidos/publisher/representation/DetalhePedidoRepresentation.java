package gabriel.tiziano.microservice_pedidos.publisher.representation;

import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public record DetalhePedidoRepresentation(
        Long codigoPedido,
        Long codigoCliente,
        String nome,
        String cpf,
        String logradouro,
        String numero,
        String bairro,
        String email,
        String telefone,
        String dataPedido,
        BigDecimal valorTotal,
        StatusPedido statusPedido,
        List<DetalheItemPedidoRepresentation> itens
) {
}
