package gabriel.tiziano.microservice_faturamento.consumer;

import gabriel.tiziano.microservice_faturamento.consumer.representation.ItemRepresentation;
import gabriel.tiziano.microservice_faturamento.consumer.representation.PedidoPagoRepresentation;
import gabriel.tiziano.microservice_faturamento.entity.ItemNotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotaFiscalMapperTest {

    @Test
    void deveMapearEventoParaNotaFiscalComSnapshotCompleto() {
        PedidoPagoRepresentation evento = new PedidoPagoRepresentation(
                100L,
                55L,
                "Gabriel Tiziano",
                "123.456.789-00",
                "Rua das Flores",
                "42",
                "Centro",
                "gabriel@email.com",
                "41999990000",
                "2026-10-17T10:30:00",
                new BigDecimal("150.00"),
                List.of(
                        new ItemRepresentation(10L, "Teclado", 2, new BigDecimal("50.00")),
                        new ItemRepresentation(20L, "Mouse", 1, new BigDecimal("50.00"))
                )
        );

        NotaFiscal nota = NotaFiscalMapper.toNotaFiscal(evento);

        assertThat(nota.getId()).isNull();
        assertThat(nota.getCodigoPedido()).isEqualTo(100L);
        assertThat(nota.getCodigoCliente()).isEqualTo(55L);
        assertThat(nota.getValorTotal()).isEqualByComparingTo("150.00");
        assertThat(nota.getStatus()).isEqualTo(StatusNotaFiscal.PROCESSANDO);
        assertThat(nota.getDataEmissao()).isNotNull();
        assertThat(nota.getChaveArquivo()).isNull();

        assertThat(nota.getDadosCliente().getNome()).isEqualTo("Gabriel Tiziano");
        assertThat(nota.getDadosCliente().getCpf()).isEqualTo("123.456.789-00");
        assertThat(nota.getDadosCliente().getEmail()).isEqualTo("gabriel@email.com");
        assertThat(nota.getDadosCliente().getTelefone()).isEqualTo("41999990000");

        assertThat(nota.getEnderecoEntrega().getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(nota.getEnderecoEntrega().getNumero()).isEqualTo("42");
        assertThat(nota.getEnderecoEntrega().getBairro()).isEqualTo("Centro");
    }

    @Test
    void deveMapearTodosOsItensEManterReferenciaParaNota() {
        PedidoPagoRepresentation evento = eventoComDoisItens();

        NotaFiscal nota = NotaFiscalMapper.toNotaFiscal(evento);

        assertThat(nota.getItens()).hasSize(2);
        assertThat(nota.getItens())
                .allSatisfy(item -> assertThat(item.getNotaFiscal()).isSameAs(nota));

        ItemNotaFiscal primeiro = nota.getItens().get(0);
        assertThat(primeiro.getCodigoProduto()).isEqualTo(10L);
        assertThat(primeiro.getNomeProduto()).isEqualTo("Teclado");
        assertThat(primeiro.getQuantidade()).isEqualTo(2);
        assertThat(primeiro.getValorUnitario()).isEqualByComparingTo("50.00");
        assertThat(primeiro.getTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveLidarComListaDeItensVazia() {
        PedidoPagoRepresentation evento = new PedidoPagoRepresentation(
                1L, 1L, "Nome", "cpf", "Logradouro", "1", "Bairro",
                "email", "tel", "2026-10-17T10:30:00", new BigDecimal("0.00"), List.of()
        );

        NotaFiscal nota = NotaFiscalMapper.toNotaFiscal(evento);

        assertThat(nota.getItens()).isEmpty();
    }

    private PedidoPagoRepresentation eventoComDoisItens() {
        return new PedidoPagoRepresentation(
                100L, 55L, "Gabriel", "123", "Rua", "42", "Centro",
                "g@email.com", "4199", "2026-10-17T10:30:00",
                new BigDecimal("150.00"),
                List.of(
                        new ItemRepresentation(10L, "Teclado", 2, new BigDecimal("50.00")),
                        new ItemRepresentation(20L, "Mouse", 1, new BigDecimal("50.00"))
                )
        );
    }
}