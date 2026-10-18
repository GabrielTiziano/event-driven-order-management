package gabriel.tiziano.microservice_faturamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotaFiscalJaEmitidaException.class)
    public ProblemDetail handleJaEmitida(NotaFiscalJaEmitidaException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(NotaFiscalNaoEncontradaException.class)
    public ProblemDetail handleNaoEncontrada(NotaFiscalNaoEncontradaException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({GeracaoNotaFiscalException.class, ArmazenamentoNotaFiscalException.class})
    public ProblemDetail handleFalhaInterna(FaturamentoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}