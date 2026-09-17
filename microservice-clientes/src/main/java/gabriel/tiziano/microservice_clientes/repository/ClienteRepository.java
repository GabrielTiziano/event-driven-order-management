package gabriel.tiziano.microservice_clientes.repository;

import gabriel.tiziano.microservice_clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
