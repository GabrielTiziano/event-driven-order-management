package gabriel.tiziano.microservice_pedidos.validator;

import feign.FeignException;
import gabriel.tiziano.microservice_pedidos.client.ClientesClient;
import gabriel.tiziano.microservice_pedidos.client.ProdutosClient;
import gabriel.tiziano.microservice_pedidos.entity.MetodoPagamento;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.exception.ClienteNotFoundException;
import gabriel.tiziano.microservice_pedidos.exception.PagamentoInvalidoException;
import gabriel.tiziano.microservice_pedidos.exception.ProdutoNotFoundException;
import gabriel.tiziano.microservice_pedidos.exception.TransicaoStatusInvalidaException;
import org.springframework.stereotype.Component;

@Component
public class PedidoValidator {

    private final ClientesClient clientesClient;
    private final ProdutosClient produtosClient;

    public PedidoValidator(ClientesClient clientesClient, ProdutosClient produtosClient) {
        this.clientesClient = clientesClient;
        this.produtosClient = produtosClient;
    }

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

    private void validarCliente(Long codigoCliente) {
        try {
            clientesClient.findClientById(codigoCliente);
        } catch (FeignException.NotFound e) {
            throw new ClienteNotFoundException(codigoCliente);
        }
    }

    private void validarProduto(Long codigoProduto) {
        try {
            produtosClient.findProductById(codigoProduto);
        } catch (FeignException.NotFound e) {
            throw new ProdutoNotFoundException(codigoProduto);
        }
    }
}