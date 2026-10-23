package gabriel.tiziano.microservice_logistica.controller;

import gabriel.tiziano.microservice_logistica.dto.DespachoResponse;
import gabriel.tiziano.microservice_logistica.service.LogisticaService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final LogisticaService logisticaService;

    public EntregaController(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    @PostMapping("/{codigoPedido}/despachar")
    public DespachoResponse despachar(@PathVariable Long codigoPedido) {
        String codigoRastreio = logisticaService.despachar(codigoPedido);
        return new DespachoResponse(codigoPedido, codigoRastreio);
    }
}