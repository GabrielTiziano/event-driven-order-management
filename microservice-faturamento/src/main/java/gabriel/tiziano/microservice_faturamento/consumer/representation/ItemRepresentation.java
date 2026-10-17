package gabriel.tiziano.microservice_faturamento.consumer.representation;

import java.math.BigDecimal;

public record ItemRepresentation(
        Long codigoProduto,
        String nomeProduto,
        Integer quantidade,
        BigDecimal valorUnitario
) {
    public BigDecimal getTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}