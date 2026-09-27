package gabriel.tiziano.microservice_pedidos.controller;

import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll() {
        return ResponseEntity.ok(pedidoService.findAllPedidos());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable Long codigo) {
        return ResponseEntity.ok(pedidoService.findPedidoById(codigo));
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.createPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<PedidoResponse> update(@PathVariable Long codigo,
                                                 @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.updatePedido(codigo, request));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable Long codigo) {
        pedidoService.deletePedido(codigo);
        return ResponseEntity.noContent().build();
    }
}