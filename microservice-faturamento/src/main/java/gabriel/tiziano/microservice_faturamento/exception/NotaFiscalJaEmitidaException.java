package gabriel.tiziano.microservice_faturamento.exception;

public class NotaFiscalJaEmitidaException extends FaturamentoException {

    public NotaFiscalJaEmitidaException(Long codigoPedido) {
        super("Nota fiscal já emitida para o pedido " + codigoPedido);
    }
}