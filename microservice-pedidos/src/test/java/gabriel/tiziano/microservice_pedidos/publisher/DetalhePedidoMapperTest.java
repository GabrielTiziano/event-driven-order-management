package gabriel.tiziano.microservice_pedidos.publisher;

import gabriel.tiziano.microservice_pedidos.entity.DadosCliente;
import gabriel.tiziano.microservice_pedidos.entity.Endereco;
import gabriel.tiziano.microservice_pedidos.entity.ItemPedido;
import gabriel.tiziano.microservice_pedidos.entity.Pedido;
import gabriel.tiziano.microservice_pedidos.entity.StatusPedido;
import gabriel.tiziano.microservice_pedidos.publisher.representation.DetalhePedidoRepresentation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DetalhePedidoMapperTest {

    @Test
    void toDetalhePedido_deveMapearSnapshotDeClienteEItens() {
        Pedido pedido = new Pedido();
        pedido.setCodigo(1L);
        pedido.setCodigoCliente(99L);
        pedido.setDataPedido(LocalDateTime.of(2026, 1, 1, 10, 0));
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setTotal(new BigDecimal("20.00"));
        pedido.setDadosCliente(new DadosCliente("Fulano", "12345678901", "fulano@email.com", "41999999999"));
        pedido.setEnderecoEntrega(new Endereco("Rua A", "100", "Centro", "Curitiba", "80000000"));

        ItemPedido item = new ItemPedido();
        item.setCodigoProduto(10L);
        item.setNomeProduto("Produto A");
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("10.00"));
        pedido.addItem(item);

        DetalhePedidoRepresentation detalhe = DetalhePedidoMapper.toDetalhePedido(pedido);

        assertThat(detalhe.codigoPedido()).isEqualTo(1L);
        assertThat(detalhe.nome()).isEqualTo("Fulano");
        assertThat(detalhe.cpf()).isEqualTo("12345678901");
        assertThat(detalhe.email()).isEqualTo("fulano@email.com");
        assertThat(detalhe.telefone()).isEqualTo("41999999999");
        assertThat(detalhe.logradouro()).isEqualTo("Rua A");
        assertThat(detalhe.statusPedido()).isEqualTo(StatusPedido.PAGO);
        assertThat(detalhe.itens()).hasSize(1);
        assertThat(detalhe.itens().get(0).nomeProduto()).isEqualTo("Produto A");
        assertThat(detalhe.itens().get(0).getTotal()).isEqualByComparingTo("20.00");
    }

    @Test
    void toDetalhePedido_semDadosCliente_naoDeveQuebrar() {
        Pedido pedido = new Pedido();
        pedido.setCodigo(1L);
        pedido.setCodigoCliente(99L);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setTotal(BigDecimal.ZERO);

        DetalhePedidoRepresentation detalhe = DetalhePedidoMapper.toDetalhePedido(pedido);

        assertThat(detalhe.nome()).isNull();
        assertThat(detalhe.logradouro()).isNull();
        assertThat(detalhe.itens()).isEmpty();
    }
}