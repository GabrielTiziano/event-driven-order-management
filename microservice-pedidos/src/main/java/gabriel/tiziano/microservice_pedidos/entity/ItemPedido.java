package gabriel.tiziano.microservice_pedidos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "codigo_produto", nullable = false)
    private Long codigoProduto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 16, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "nome_produto", length = 255)
    private String nomeProduto;
}