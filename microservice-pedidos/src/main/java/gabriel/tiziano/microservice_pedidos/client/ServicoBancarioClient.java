package gabriel.tiziano.microservice_pedidos.client;

import gabriel.tiziano.microservice_pedidos.entity.Pedido;

public interface ServicoBancarioClient {
    String solicitarPagamento(Pedido pedido);
}