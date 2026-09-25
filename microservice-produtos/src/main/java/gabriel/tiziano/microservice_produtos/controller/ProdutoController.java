package gabriel.tiziano.microservice_produtos.controller;

import gabriel.tiziano.microservice_produtos.dto.ProdutoRequest;
import gabriel.tiziano.microservice_produtos.dto.ProdutoResponse;
import gabriel.tiziano.microservice_produtos.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> findAllProducts() {
        return ResponseEntity.ok(produtoService.findAllProducts());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProdutoResponse> findProductById(@PathVariable Long codigo) {
        return ResponseEntity.ok(produtoService.findProductById(codigo));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> saveProduct(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.createProduct(request));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProdutoResponse> updateProduct(@PathVariable Long codigo,
                                                         @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.updateProduct(codigo, request));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long codigo) {
        produtoService.deleteProduct(codigo);
        return ResponseEntity.noContent().build();
    }
}
