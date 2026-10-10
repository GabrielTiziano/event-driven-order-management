package gabriel.tiziano.microservice_pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DadosCliente {

    @Column(name = "cliente_nome", length = 255)
    private String nome;

    @Column(name = "cliente_cpf", length = 14)
    private String cpf;

    @Column(name = "cliente_email", length = 255)
    private String email;

    @Column(name = "cliente_telefone", length = 20)
    private String telefone;
}