package gabriel.tiziano.microservice_pedidos.repository;

import gabriel.tiziano.microservice_pedidos.entity.CallbackProcessado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackProcessadoRepository extends JpaRepository<CallbackProcessado, String> {
}
