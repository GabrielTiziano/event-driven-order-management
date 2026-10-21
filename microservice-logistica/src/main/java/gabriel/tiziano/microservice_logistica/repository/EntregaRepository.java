package gabriel.tiziano.microservice_logistica.repository;

import gabriel.tiziano.microservice_logistica.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    boolean existsByCodigoPedido(Long codigoPedido);
    Optional<Entrega> findByCodigoPedido(Long codigoPedido);
}