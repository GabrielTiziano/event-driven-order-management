package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.exception.PedidoNotFoundException;
import gabriel.tiziano.microservice_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido novoPedido(Long codigo) {
        return new Pedido(
                codigo, 1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "REALIZADO");
    }

    private PedidoRequest novoRequest() {
        return new PedidoRequest(
                1L, 2L, 3,
                new BigDecimal("10.00"), new BigDecimal("30.00"),
                "REALIZADO");
    }

    @Test
    void findAllPedidos_deveRetornarListaDeResponses() {
        when(pedidoRepository.findAll()).thenReturn(List.of(novoPedido(1L), novoPedido(2L)));

        List<PedidoResponse> responses = pedidoService.findAllPedidos();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).codigo()).isEqualTo(1L);
        assertThat(responses.get(1).codigo()).isEqualTo(2L);
        verify(pedidoRepository).findAll();
    }

    @Test
    void findPedidoById_quandoExiste_deveRetornarResponse() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(novoPedido(1L)));

        PedidoResponse response = pedidoService.findPedidoById(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo("REALIZADO");
    }

    @Test
    void findPedidoById_quandoNaoExiste_deveLancarExcecao() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findPedidoById(99L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createPedido_devePersistirERetornarResponse() {
        PedidoRequest request = novoRequest();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(novoPedido(10L));

        PedidoResponse response = pedidoService.createPedido(request);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        assertThat(captor.getValue().getCodigo()).isNull();
        assertThat(captor.getValue().getCodigoCliente()).isEqualTo(1L);
        assertThat(response.codigo()).isEqualTo(10L);
    }

    @Test
    void updatePedido_quandoExiste_deveAtualizarERetornarResponse() {
        Pedido existente = novoPedido(1L);
        PedidoRequest request = new PedidoRequest(
                5L, 6L, 4,
                new BigDecimal("15.00"), new BigDecimal("60.00"),
                "PAGO");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(existente);

        PedidoResponse response = pedidoService.updatePedido(1L, request);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.codigoCliente()).isEqualTo(5L);
        assertThat(response.status()).isEqualTo("PAGO");
        verify(pedidoRepository).save(existente);
    }

    @Test
    void updatePedido_quandoNaoExiste_deveLancarExcecao() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.updatePedido(99L, novoRequest()))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("99");

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deletePedido_quandoExiste_deveDeletar() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        pedidoService.deletePedido(1L);

        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void deletePedido_quandoNaoExiste_deveLancarExcecao() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pedidoService.deletePedido(99L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("99");

        verify(pedidoRepository, never()).deleteById(any());
    }
}