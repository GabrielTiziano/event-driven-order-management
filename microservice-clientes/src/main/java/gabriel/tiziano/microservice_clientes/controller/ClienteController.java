package gabriel.tiziano.microservice_clientes.controller;

import gabriel.tiziano.microservice_clientes.dto.ClienteRequest;
import gabriel.tiziano.microservice_clientes.dto.ClienteResponse;
import gabriel.tiziano.microservice_clientes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAllClients() {
        return ResponseEntity.ok(clienteService.findAllClients());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ClienteResponse> findClientById(@PathVariable Long codigo) {
        return ResponseEntity.ok(clienteService.findClientById(codigo));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> saveClient(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.createClient(request));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ClienteResponse> updateClient(@PathVariable Long codigo,
                                                        @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.updateClient(codigo, request));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long codigo) {
        clienteService.deleteClient(codigo);
        return ResponseEntity.noContent().build();
    }
}