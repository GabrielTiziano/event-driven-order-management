package gabriel.tiziano.microservice_faturamento.consumer.representation;

import java.math.BigDecimal;
import java.util.List;

public record PedidoPagoRepresentation(
        Long codigoPedido,
        Long codigoCliente,
        String nome,
        String cpf,
        String logradouro,
        String numero,
        String bairro,
        String email,
        String telefone,
        String dataPedido,
        BigDecimal valorTotal,
        List<ItemRepresentation> itens
) {
}