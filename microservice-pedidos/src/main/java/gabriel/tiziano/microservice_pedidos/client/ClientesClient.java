package gabriel.tiziano.microservice_pedidos.client;

import gabriel.tiziano.microservice_pedidos.client.representation.ClienteRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes", url = "${order-management-microservices.microservice-clientes.url}")
public interface ClientesClient {
    @GetMapping("/clientes/{codigo}")
    ResponseEntity<ClienteRepresentation> findClientById(@PathVariable Long codigo);
}
