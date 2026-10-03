package gabriel.tiziano.microservice_pedidos.controller;

import gabriel.tiziano.microservice_pedidos.dto.CallbackPagamentoRequest;
import gabriel.tiziano.microservice_pedidos.exception.WebhookNaoAutorizadoException;
import gabriel.tiziano.microservice_pedidos.service.CallbackPagamentoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/webhooks/pagamentos")
public class CallbackPagamentoController {
    private final CallbackPagamentoService callbackPagamentoService;
    private final String apiKeyEsperada;

    public CallbackPagamentoController(CallbackPagamentoService callbackPagamentoService,
                                       @Value("${webhook.payments.api-key}") String apiKeyEsperada) {
        this.callbackPagamentoService = callbackPagamentoService;
        this.apiKeyEsperada = apiKeyEsperada;
    }

    @PostMapping
    public ResponseEntity<Void> receive(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CallbackPagamentoRequest callback) {
        validateApiKey(apiKey);
        callbackPagamentoService.process(idempotencyKey, callback);
        return ResponseEntity.ok().build();
    }

    private void validateApiKey(String apiKey) {
        if (apiKey == null || !MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                apiKeyEsperada.getBytes(StandardCharsets.UTF_8))) {
            throw new WebhookNaoAutorizadoException("API key inválida");
        }
    }
}
