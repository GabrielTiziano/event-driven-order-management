package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.consumer.representation.ItemRepresentation;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import gabriel.tiziano.microservice_faturamento.exception.ArmazenamentoNotaFiscalException;
import gabriel.tiziano.microservice_faturamento.repository.NotaFiscalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaturamentoServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private GeradorNotaFiscalService geradorNotaFiscalService;
    @Mock
    private BucketService bucketService;

    @InjectMocks
    private FaturamentoService faturamentoService;

    @Test
    void naoDeveFaturarQuandoNotaJaExiste() {
        PedidoPagoRepresentation evento = evento(100L);
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(true);

        faturamentoService.faturar(evento);

        verifyNoInteractions(geradorNotaFiscalService, bucketService);
        verify(notaFiscalRepository, never()).save(any());
    }

    @Test
    void deveGerarFazerUploadETornarNotaEmitidaNoFluxoFeliz() {
        PedidoPagoRepresentation evento = evento(100L);
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(false);
        when(geradorNotaFiscalService.gerarNotaFiscal(any())).thenReturn(new byte[]{1, 2, 3});

        faturamentoService.faturar(evento);

        verify(bucketService).upload(eq("nota-fiscal-100.pdf"), any(byte[].class), eq("application/pdf"));

        ArgumentCaptor<NotaFiscal> captor = ArgumentCaptor.forClass(NotaFiscal.class);
        verify(notaFiscalRepository).save(captor.capture());
        NotaFiscal salva = captor.getValue();
        assertThat(salva.getStatus()).isEqualTo(StatusNotaFiscal.EMITIDA);
        assertThat(salva.getChaveArquivo()).isEqualTo("nota-fiscal-100.pdf");
        assertThat(salva.getCodigoPedido()).isEqualTo(100L);
    }

    @Test
    void deveLancarArmazenamentoExceptionENaoSalvarQuandoUploadFalha() {
        PedidoPagoRepresentation evento = evento(100L);
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(false);
        when(geradorNotaFiscalService.gerarNotaFiscal(any())).thenReturn(new byte[]{1});
        doThrow(new RuntimeException("falha no storage"))
                .when(bucketService).upload(anyString(), any(byte[].class), anyString());

        assertThatThrownBy(() -> faturamentoService.faturar(evento))
                .isInstanceOf(ArmazenamentoNotaFiscalException.class);

        verify(notaFiscalRepository, never()).save(any());
    }

    private PedidoPagoRepresentation evento(Long codigoPedido) {
        return new PedidoPagoRepresentation(
                codigoPedido, 55L, "Gabriel", "123", "Rua", "42", "Centro",
                "g@email.com", "4199", "2026-10-18T10:00:00",
                new BigDecimal("150.00"),
                List.of(new ItemRepresentation(10L, "Teclado", 2, new BigDecimal("75.00")))
        );
    }
}