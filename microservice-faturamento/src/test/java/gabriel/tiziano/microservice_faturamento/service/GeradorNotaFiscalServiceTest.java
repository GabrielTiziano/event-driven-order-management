package gabriel.tiziano.microservice_faturamento.service;

import gabriel.tiziano.microservice_faturamento.entity.DadosCliente;
import gabriel.tiziano.microservice_faturamento.entity.EnderecoEntrega;
import gabriel.tiziano.microservice_faturamento.entity.ItemNotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GeradorNotaFiscalServiceTest {

    private GeradorNotaFiscalService gerador;

    @BeforeEach
    void setUp() {
        gerador = new GeradorNotaFiscalService();
        gerador.compilarTemplate();
    }

    @Test
    void deveGerarUmPdfValidoAPartirDaNota() {
        byte[] pdf = gerador.gerarNotaFiscal(notaDeExemplo());

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    private NotaFiscal notaDeExemplo() {
        NotaFiscal nota = new NotaFiscal();
        nota.setCodigoPedido(100L);
        nota.setCodigoCliente(55L);
        nota.setDadosCliente(new DadosCliente("Gabriel", "123.456.789-00", "g@email.com", "4199"));
        nota.setEnderecoEntrega(new EnderecoEntrega("Rua das Flores", "42", "Centro"));
        nota.setValorTotal(new BigDecimal("150.00"));
        nota.setStatus(StatusNotaFiscal.PROCESSANDO);
        nota.setDataEmissao(LocalDateTime.now());

        ItemNotaFiscal item = new ItemNotaFiscal();
        item.setCodigoProduto(10L);
        item.setNomeProduto("Teclado");
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("75.00"));
        nota.addItem(item);

        return nota;
    }
}