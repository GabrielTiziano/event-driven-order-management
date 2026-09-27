package gabriel.tiziano.microservice_pedidos.service;

import gabriel.tiziano.microservice_pedidos.dto.PedidoItemRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoRequest;
import gabriel.tiziano.microservice_pedidos.dto.PedidoResponse;
import gabriel.tiziano.microservice_pedidos.dto.PedidoStatusRequest;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
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
import java.time.LocalDateTime;
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

    private Pedido pedidoSalvo(Long codigo, String status) {
        Pedido pedido = new Pedido();
        pedido.setCodigo(codigo);
        pedido.setCodigoCliente(1L);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setTotal(new BigDecimal("20.00"));

        ItemPedido item = new ItemPedido();
        item.setCodigo(500L);
        item.setCodigoProduto(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        return pedido;
    }

    private PedidoRequest novoRequest() {
        return new PedidoRequest(
                1L, "obs",
                List.of(new PedidoItemRequest(10L, 2, new BigDecimal("10.00"))));
    }

    @Test
    void findAllPedidos_deveRetornarListaDeResponses() {
        when(pedidoRepository.findAll())
                .thenReturn(List.of(pedidoSalvo(1L, "REALIZADO"), pedidoSalvo(2L, "PAGO")));

        List<PedidoResponse> responses = pedidoService.findAllPedidos();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).codigo()).isEqualTo(1L);
        assertThat(responses.get(1).status()).isEqualTo("PAGO");
        verify(pedidoRepository).findAll();
    }

    @Test
    void findPedidoById_quandoExiste_deveRetornarResponse() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoSalvo(1L, "REALIZADO")));

        PedidoResponse response = pedidoService.findPedidoById(1L);

        assertThat(response.codigo()).isEqualTo(1L);
        assertThat(response.itens()).hasSize(1);
    }

    @Test
    void findPedidoById_quandoNaoExiste_deveLancarExcecao() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findPedidoById(99L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createPedido_devePersistirComStatusInicialERetornarResponse() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo(10L, "REALIZADO"));

        PedidoResponse response = pedidoService.createPedido(novoRequest());

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        Pedido enviado = captor.getValue();

        assertThat(enviado.getCodigo()).isNull();
        assertThat(enviado.getStatus()).isEqualTo("REALIZADO");
        assertThat(enviado.getItens()).hasSize(1);
        assertThat(response.codigo()).isEqualTo(10L);
    }

    @Test
    void updateStatus_quandoExiste_deveAtualizarStatus() {
        Pedido existente = pedidoSalvo(1L, "REALIZADO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(existente);

        PedidoResponse response = pedidoService.updateStatus(1L, new PedidoStatusRequest("PAGO"));

        assertThat(existente.getStatus()).isEqualTo("PAGO");
        assertThat(response.status()).isEqualTo("PAGO");
        verify(pedidoRepository).save(existente);
    }

    @Test
    void updateStatus_quandoNaoExiste_deveLancarExcecao() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.updateStatus(99L, new PedidoStatusRequest("PAGO")))
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