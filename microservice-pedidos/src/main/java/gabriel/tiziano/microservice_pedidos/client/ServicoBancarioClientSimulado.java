package gabriel.tiziano.microservice_pedidos.client;

import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ServicoBancarioClientSimulado implements ServicoBancarioClient {

    @Override
    public String solicitarPagamento(Pedido pedido) {
        String chavePagamento = UUID.randomUUID().toString();
        log.info("Pagamento solicitado [cliente={}, total={}, metodo={}] -> chave={}",
                pedido.getCodigoCliente(), pedido.getTotal(), pedido.getMetodoPagamento(), chavePagamento);
        return chavePagamento;
    }
}