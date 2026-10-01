package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class ProdutoNotFoundException extends RegraNegocioException {
    public ProdutoNotFoundException(Long codigo) {
        super("Produto não encontrado com o código: " + codigo, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}