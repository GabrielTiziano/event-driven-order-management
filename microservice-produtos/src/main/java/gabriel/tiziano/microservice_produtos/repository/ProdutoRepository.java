package gabriel.tiziano.microservice_produtos.repository;

import gabriel.tiziano.microservice_produtos.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
