package gabriel.tiziano.microservice_logistica.service;

import gabriel.tiziano.microservice_logistica.entity.Entrega;
import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;
import gabriel.tiziano.microservice_logistica.exception.EntregaNaoEncontradaException;
import gabriel.tiziano.microservice_logistica.exception.EstadoEntregaInvalidoException;
import gabriel.tiziano.microservice_logistica.publisher.LogisticaPublisher;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoEnviadoRepresentation;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_logistica.repository.EntregaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;
    @Mock
    private LogisticaPublisher logisticaPublisher;

    @InjectMocks
    private LogisticaService logisticaService;

    @Test
    void naoDeveCriarEntregaQuandoJaExiste() {
        when(entregaRepository.existsByCodigoPedido(1L)).thenReturn(true);

        logisticaService.receberParaPreparacao(1L);

        verify(entregaRepository, never()).save(any());
        verifyNoInteractions(logisticaPublisher);
    }

    @Test
    void deveCriarEntregaEmPreparacaoEPublicarEvento() {
        when(entregaRepository.existsByCodigoPedido(1L)).thenReturn(false);

        logisticaService.receberParaPreparacao(1L);

        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(entregaRepository).save(entregaCaptor.capture());
        Entrega salva = entregaCaptor.getValue();
        assertThat(salva.getCodigoPedido()).isEqualTo(1L);
        assertThat(salva.getStatus()).isEqualTo(StatusEntrega.EM_PREPARACAO);
        assertThat(salva.getDataCriacao()).isNotNull();
        assertThat(salva.getCodigoRastreio()).isNull();

        ArgumentCaptor<PedidoPreparandoEnvioRepresentation> eventoCaptor =
                ArgumentCaptor.forClass(PedidoPreparandoEnvioRepresentation.class);
        verify(logisticaPublisher).publicarPreparando(eventoCaptor.capture());
        assertThat(eventoCaptor.getValue().codigoPedido()).isEqualTo(1L);
    }

    @Test
    void despacharDeveGerarRastreioMarcarDespachadoEPublicarEnviado() {
        Entrega entrega = new Entrega();
        entrega.setCodigoPedido(1L);
        entrega.setStatus(StatusEntrega.EM_PREPARACAO);
        entrega.setDataCriacao(LocalDateTime.now());
        when(entregaRepository.findByCodigoPedido(1L)).thenReturn(Optional.of(entrega));

        String rastreio = logisticaService.despachar(1L);

        assertThat(rastreio).matches("LG\\d{9}BR");
        assertThat(entrega.getStatus()).isEqualTo(StatusEntrega.DESPACHADO);
        assertThat(entrega.getCodigoRastreio()).isEqualTo(rastreio);
        assertThat(entrega.getDataDespacho()).isNotNull();
        verify(entregaRepository).save(entrega);

        ArgumentCaptor<PedidoEnviadoRepresentation> captor =
                ArgumentCaptor.forClass(PedidoEnviadoRepresentation.class);
        verify(logisticaPublisher).publicarEnviado(captor.capture());
        assertThat(captor.getValue().codigoPedido()).isEqualTo(1L);
        assertThat(captor.getValue().codigoRastreio()).isEqualTo(rastreio);
    }

    @Test
    void despacharDeveLancarQuandoEntregaNaoExiste() {
        when(entregaRepository.findByCodigoPedido(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logisticaService.despachar(99L))
                .isInstanceOf(EntregaNaoEncontradaException.class);

        verify(entregaRepository, never()).save(any());
        verify(logisticaPublisher, never()).publicarEnviado(any());
    }

    @Test
    void despacharDeveLancarQuandoEntregaNaoEstaEmPreparacao() {
        Entrega entrega = new Entrega();
        entrega.setCodigoPedido(1L);
        entrega.setStatus(StatusEntrega.DESPACHADO);
        when(entregaRepository.findByCodigoPedido(1L)).thenReturn(Optional.of(entrega));

        assertThatThrownBy(() -> logisticaService.despachar(1L))
                .isInstanceOf(EstadoEntregaInvalidoException.class);

        verify(entregaRepository, never()).save(any());
        verify(logisticaPublisher, never()).publicarEnviado(any());
    }
}