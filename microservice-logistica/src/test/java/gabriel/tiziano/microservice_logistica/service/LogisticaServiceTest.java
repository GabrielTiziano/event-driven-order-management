package gabriel.tiziano.microservice_logistica.service;

import gabriel.tiziano.microservice_logistica.LogisticaService;
import gabriel.tiziano.microservice_logistica.entity.Entrega;
import gabriel.tiziano.microservice_logistica.entity.StatusEntrega;
import gabriel.tiziano.microservice_logistica.publisher.LogisticaPublisher;
import gabriel.tiziano.microservice_logistica.publisher.representation.PedidoPreparandoEnvioRepresentation;
import gabriel.tiziano.microservice_logistica.repository.EntregaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
}