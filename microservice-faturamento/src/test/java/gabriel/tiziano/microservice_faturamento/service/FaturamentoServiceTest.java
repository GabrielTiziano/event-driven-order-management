package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.consumer.representation.ItemRepresentation;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import gabriel.tiziano.microservice_faturamento.exception.ArmazenamentoNotaFiscalException;
import gabriel.tiziano.microservice_faturamento.exception.GeracaoNotaFiscalException;
import gabriel.tiziano.microservice_faturamento.publisher.FaturamentoPublisher;
import gabriel.tiziano.microservice_faturamento.publisher.representation.PedidoFaturadoRepresentation;
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
    @Mock
    private FaturamentoPublisher faturamentoPublisher;

    @InjectMocks
    private FaturamentoService faturamentoService;

    @Test
    void naoDeveFaturarQuandoNotaJaExiste() {
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(true);

        faturamentoService.faturar(evento(100L));

        verifyNoInteractions(geradorNotaFiscalService, bucketService, faturamentoPublisher);
        verify(notaFiscalRepository, never()).save(any());
    }

    @Test
    void deveEmitirNotaEPublicarEventoNoFluxoFeliz() {
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(false);
        when(geradorNotaFiscalService.gerarNotaFiscal(any())).thenReturn(new byte[]{1, 2, 3});

        faturamentoService.faturar(evento(100L));

        verify(bucketService).upload(eq("nota-fiscal-100.pdf"), any(byte[].class), eq("application/pdf"));

        ArgumentCaptor<NotaFiscal> notaCaptor = ArgumentCaptor.forClass(NotaFiscal.class);
        verify(notaFiscalRepository).save(notaCaptor.capture());
        NotaFiscal salva = notaCaptor.getValue();
        assertThat(salva.getStatus()).isEqualTo(StatusNotaFiscal.EMITIDA);
        assertThat(salva.getChaveArquivo()).isEqualTo("nota-fiscal-100.pdf");

        ArgumentCaptor<PedidoFaturadoRepresentation> eventoCaptor =
                ArgumentCaptor.forClass(PedidoFaturadoRepresentation.class);
        verify(faturamentoPublisher).publicar(eventoCaptor.capture());
        assertThat(eventoCaptor.getValue().codigoPedido()).isEqualTo(100L);
        assertThat(eventoCaptor.getValue().chaveArquivo()).isEqualTo("nota-fiscal-100.pdf");
    }

    @Test
    void deveLancarArmazenamentoExceptionENaoSalvarNemPublicarQuandoUploadFalha() {
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(false);
        when(geradorNotaFiscalService.gerarNotaFiscal(any())).thenReturn(new byte[]{1});
        doThrow(new RuntimeException("falha no storage"))
                .when(bucketService).upload(anyString(), any(byte[].class), anyString());

        assertThatThrownBy(() -> faturamentoService.faturar(evento(100L)))
                .isInstanceOf(ArmazenamentoNotaFiscalException.class);

        verify(notaFiscalRepository, never()).save(any());
        verify(faturamentoPublisher, never()).publicar(any());
    }

    @Test
    void devePropagarGeracaoExceptionENaoSubirSalvarNemPublicarQuandoGeracaoFalha() {
        when(notaFiscalRepository.existsByCodigoPedido(100L)).thenReturn(false);
        when(geradorNotaFiscalService.gerarNotaFiscal(any()))
                .thenThrow(new GeracaoNotaFiscalException(100L, new RuntimeException("falha jasper")));

        assertThatThrownBy(() -> faturamentoService.faturar(evento(100L)))
                .isInstanceOf(GeracaoNotaFiscalException.class);

        verifyNoInteractions(bucketService, faturamentoPublisher);
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