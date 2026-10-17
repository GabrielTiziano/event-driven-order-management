package gabriel.tiziano.microservice_faturamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "nota_fiscal",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_nota_fiscal_codigo_pedido",
                columnNames = "codigo_pedido"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_pedido", nullable = false, unique = true)
    private Long codigoPedido;

    @Column(name = "codigo_cliente", nullable = false)
    private Long codigoCliente;

    @Embedded
    private DadosCliente dadosCliente;

    @Embedded
    private EnderecoEntrega enderecoEntrega;

    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusNotaFiscal status;

    @Column(name = "chave_arquivo")
    private String chaveArquivo;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNotaFiscal> itens = new ArrayList<>();

    public void addItem(ItemNotaFiscal item) {
        item.setNotaFiscal(this);
        this.itens.add(item);
    }
}