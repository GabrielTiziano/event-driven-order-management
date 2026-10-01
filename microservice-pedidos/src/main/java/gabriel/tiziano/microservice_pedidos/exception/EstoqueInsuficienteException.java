package gabriel.tiziano.microservice_pedidos.exception;

import org.springframework.http.HttpStatus;

public class EstoqueInsuficienteException extends RegraNegocioException {
    public EstoqueInsuficienteException(Long codigoProduto) {
        super("Estoque insuficiente para o produto: " + codigoProduto, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}