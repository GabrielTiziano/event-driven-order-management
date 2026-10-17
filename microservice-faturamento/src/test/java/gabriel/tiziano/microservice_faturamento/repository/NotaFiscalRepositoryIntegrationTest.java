package gabriel.tiziano.microservice_faturamento.repository;

import gabriel.tiziano.microservice_faturamento.entity.DadosCliente;
import gabriel.tiziano.microservice_faturamento.entity.EnderecoEntrega;
import gabriel.tiziano.microservice_faturamento.entity.ItemNotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.NotaFiscal;
import gabriel.tiziano.microservice_faturamento.entity.StatusNotaFiscal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class NotaFiscalRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private NotaFiscalRepository repository;

    @Test
    void deveValidarSchemaDaMigrationEPersistirNotaComItensEmCascata() {
        NotaFiscal nota = novaNota(1000L);
        nota.addItem(item(10L, "Teclado", 2, "50.00"));
        nota.addItem(item(20L, "Mouse", 1, "50.00"));

        NotaFiscal salva = repository.saveAndFlush(nota);

        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getItens()).hasSize(2);
        assertThat(salva.getItens())
                .allSatisfy(item -> assertThat(item.getId()).isNotNull());
    }

    @Test
    void existsByCodigoPedidoDeveRefletirPresencaNoBanco() {
        assertThat(repository.existsByCodigoPedido(2000L)).isFalse();

        repository.saveAndFlush(novaNota(2000L));

        assertThat(repository.existsByCodigoPedido(2000L)).isTrue();
    }

    @Test
    void deveRejeitarNotaDuplicadaPeloMesmoCodigoPedido() {
        repository.saveAndFlush(novaNota(3000L));

        NotaFiscal duplicada = novaNota(3000L);

        assertThatThrownBy(() -> repository.saveAndFlush(duplicada))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private NotaFiscal novaNota(Long codigoPedido) {
        NotaFiscal nota = new NotaFiscal();
        nota.setCodigoPedido(codigoPedido);
        nota.setCodigoCliente(55L);
        nota.setDadosCliente(new DadosCliente("Gabriel", "123", "g@email.com", "4199"));
        nota.setEnderecoEntrega(new EnderecoEntrega("Rua", "42", "Centro"));
        nota.setValorTotal(new BigDecimal("100.00"));
        nota.setStatus(StatusNotaFiscal.PROCESSANDO);
        nota.setDataEmissao(LocalDateTime.now());
        return nota;
    }

    private ItemNotaFiscal item(Long codigo, String nome, int qtd, String valor) {
        ItemNotaFiscal item = new ItemNotaFiscal();
        item.setCodigoProduto(codigo);
        item.setNomeProduto(nome);
        item.setQuantidade(qtd);
        item.setValorUnitario(new BigDecimal(valor));
        return item;
    }
}