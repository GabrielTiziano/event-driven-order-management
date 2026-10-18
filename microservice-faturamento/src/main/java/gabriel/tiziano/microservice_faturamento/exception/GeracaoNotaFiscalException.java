package gabriel.tiziano.microservice_faturamento.exception;

public class GeracaoNotaFiscalException extends FaturamentoException {

    public GeracaoNotaFiscalException(Long codigoPedido, Throwable cause) {
        super("Falha ao gerar o documento da nota fiscal do pedido " + codigoPedido, cause);
    }
}