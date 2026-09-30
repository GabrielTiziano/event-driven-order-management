package gabriel.tiziano.microservice_pedidos.validator;

import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.PagamentoInvalidoException;
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

    public void validarPagamento(Pedido pedido) {
        if (pedido.getMetodoPagamento() == MetodoPagamento.CREDITO) {
            if (pedido.getParcelas() == null || pedido.getParcelas() < 1) {
                throw new PagamentoInvalidoException(
                        "Pagamento no crédito exige o número de parcelas (mínimo 1)");
            }
        } else if (pedido.getParcelas() != null && pedido.getParcelas() > 1) {
            throw new PagamentoInvalidoException(
                    "Parcelamento só é permitido para pagamento no crédito");
        }
    }
}