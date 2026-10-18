package gabriel.tiziano.microservice_faturamento.exception;

public class NotaFiscalNaoEncontradaException extends FaturamentoException {

    public NotaFiscalNaoEncontradaException(Long codigoPedido) {
        super("Nota fiscal não encontrada para o pedido " + codigoPedido);
    }
}