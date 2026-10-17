package gabriel.tiziano.microservice_faturamento.repository;

import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    boolean existsByCodigoPedido(Long codigoPedido);
}