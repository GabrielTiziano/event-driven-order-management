package gabriel.tiziano.microservice_logistica.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "entrega",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_entrega_codigo_pedido",
                columnNames = "codigo_pedido"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_pedido", nullable = false, unique = true)
    private Long codigoPedido;

    @Column(name = "codigo_rastreio")
    private String codigoRastreio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEntrega status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_despacho")
    private LocalDateTime dataDespacho;
}