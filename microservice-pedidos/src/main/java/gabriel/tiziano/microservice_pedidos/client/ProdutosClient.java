package gabriel.tiziano.microservice_pedidos.client;

import gabriel.tiziano.microservice_pedidos.client.representation.ProdutoRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "produtos", url = "${order-management-microservices.microservice-produtos.url}")
public interface ProdutosClient {
    @GetMapping("/produtos/{codigo}")
    public ResponseEntity<ProdutoRepresentation> findProductById(@PathVariable Long codigo);

}
