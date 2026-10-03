package gabriel.tiziano.microservice_pedidos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "callbacks_processados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallbackProcessado {
    @Id
    @Column(name = "chave_idempotencia")
    private String chaveIdempotencia;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;
}