package gabriel.tiziano.microservice_faturamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class DadosCliente {

    @Column(name = "cliente_nome", nullable = false)
    private String nome;

    @Column(name = "cliente_cpf", nullable = false)
    private String cpf;

    @Column(name = "cliente_email")
    private String email;

    @Column(name = "cliente_telefone")
    private String telefone;

    public DadosCliente(String nome, String cpf, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }
}