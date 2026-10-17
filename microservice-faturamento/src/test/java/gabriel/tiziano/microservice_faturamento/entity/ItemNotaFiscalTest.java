package gabriel.tiziano.microservice_faturamento.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ItemNotaFiscalTest {

    @Test
    void getTotalDeveMultiplicarValorUnitarioPelaQuantidade() {
        ItemNotaFiscal item = new ItemNotaFiscal();
        item.setValorUnitario(new BigDecimal("19.90"));
        item.setQuantidade(3);

        assertThat(item.getTotal()).isEqualByComparingTo("59.70");
    }
}