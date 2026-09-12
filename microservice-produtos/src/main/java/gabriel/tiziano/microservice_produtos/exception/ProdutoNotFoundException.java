package gabriel.tiziano.microservice_produtos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProdutoNotFoundException extends RuntimeException {
    public ProdutoNotFoundException(Long codigo) {
        super("Produto com código " + codigo + " não encontrado.");
    }
}