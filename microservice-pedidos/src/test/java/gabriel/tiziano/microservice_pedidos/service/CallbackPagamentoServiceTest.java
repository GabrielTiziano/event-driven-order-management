package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.dto.CallbackPagamentoRequest;
import gabriel.tiziano.microservice_pedidos.entity.CallbackProcessado;
import gabriel.tiziano.microservice_pedidos.repository.CallbackProcessadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallbackPagamentoServiceTest {

    @Mock
    private CallbackProcessadoRepository callbackProcessadoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private CallbackPagamentoService callbackPagamentoService;

    private CallbackPagamentoRequest callback() {
        return new CallbackPagamentoRequest(1L, "chave-123", true, "ok");
    }

    @Test
    void process_primeiraVez_deveRegistrarChaveEConfirmarPagamento() {
        when(callbackProcessadoRepository.existsById("idem-1")).thenReturn(false);

        callbackPagamentoService.process("idem-1", callback());

        verify(callbackProcessadoRepository).save(any(CallbackProcessado.class));
        verify(pedidoService).confirmPayment(1L, "chave-123", true);
    }

    @Test
    void process_reentrega_naoDeveReprocessar() {
        when(callbackProcessadoRepository.existsById("idem-1")).thenReturn(true);

        callbackPagamentoService.process("idem-1", callback());

        verify(callbackProcessadoRepository, never()).save(any(CallbackProcessado.class));
        verify(pedidoService, never()).confirmPayment(any(), any(), anyBoolean());
    }
}
