package gabriel.tiziano.microservice_faturamento.exception;

public class ArmazenamentoNotaFiscalException extends FaturamentoException {

    public ArmazenamentoNotaFiscalException(String chave, Throwable cause) {
        super("Falha ao armazenar o arquivo da nota fiscal (chave: " + chave + ")", cause);
    }
}