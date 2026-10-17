package gabriel.tiziano.microservice_faturamento.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotaFiscalTest {

    @Test
    void addItemDeveVincularOsDoisLadosDaRelacao() {
        NotaFiscal nota = new NotaFiscal();
        ItemNotaFiscal item = new ItemNotaFiscal();

        nota.addItem(item);

        assertThat(nota.getItens()).containsExactly(item);
        assertThat(item.getNotaFiscal()).isSameAs(nota);
    }
}