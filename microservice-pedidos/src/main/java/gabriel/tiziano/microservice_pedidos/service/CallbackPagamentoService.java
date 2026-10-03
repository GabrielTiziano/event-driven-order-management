package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.dto.CallbackPagamentoRequest;
import gabriel.tiziano.microservice_pedidos.entity.CallbackProcessado;
import gabriel.tiziano.microservice_pedidos.repository.CallbackProcessadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CallbackPagamentoService {
    private final CallbackProcessadoRepository callbackProcessadoRepository;
    private final PedidoService pedidoService;

    public CallbackPagamentoService(CallbackProcessadoRepository callbackProcessadoRepository, PedidoService pedidoService) {
        this.callbackProcessadoRepository = callbackProcessadoRepository;
        this.pedidoService = pedidoService;
    }

    @Transactional
    public void process(String idempotencyKey, CallbackPagamentoRequest callbackPagamento) {
        if (callbackProcessadoRepository.existsById(idempotencyKey)) {
            return;
        }
        callbackProcessadoRepository.save(new CallbackProcessado(idempotencyKey, LocalDateTime.now()));
        pedidoService.confirmPayment(callbackPagamento.codigo(), callbackPagamento.chavePagamento(), callbackPagamento.aprovado());
    }
}
