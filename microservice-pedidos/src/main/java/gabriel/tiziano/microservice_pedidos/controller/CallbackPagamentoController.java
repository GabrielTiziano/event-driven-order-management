package gabriel.tiziano.microservice_pedidos.controller;

import gabriel.tiziano.microservice_pedidos.dto.CallbackPagamentoRequest;
import gabriel.tiziano.microservice_pedidos.service.CallbackPagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/pagamentos")
public class CallbackPagamentoController {
    private final CallbackPagamentoService paymentWebhookService;

    public CallbackPagamentoController(CallbackPagamentoService paymentWebhookService) {
        this.paymentWebhookService = paymentWebhookService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CallbackPagamentoRequest callback) {
        paymentWebhookService.process(idempotencyKey, callback);
        return ResponseEntity.ok().build();
    }
}
