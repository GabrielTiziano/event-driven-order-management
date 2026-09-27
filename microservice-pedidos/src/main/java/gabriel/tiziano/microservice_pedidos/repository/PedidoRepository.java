package gabriel.tiziano.microservice_pedidos.repository;

import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
