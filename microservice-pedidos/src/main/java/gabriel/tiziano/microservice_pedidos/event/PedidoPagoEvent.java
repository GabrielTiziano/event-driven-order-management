package gabriel.tiziano.microservice_pedidos.event;

import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;

public record PedidoPagoEvent(DetalhePedidoRepresentation detalhePedidoRepresentation) {
}
