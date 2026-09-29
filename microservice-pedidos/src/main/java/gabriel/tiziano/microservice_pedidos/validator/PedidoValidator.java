package gabriel.tiziano.microservice_pedidos.validator;

import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.TransicaoStatusInvalidaException;
import org.springframework.stereotype.Component;

@Component
public class PedidoValidator {

    public void validarTransicaoStatus(Pedido pedido, StatusPedido novoStatus) {
        StatusPedido atual = pedido.getStatus();
        if (!atual.podeTransicionarPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(atual, novoStatus);
        }
    }
}