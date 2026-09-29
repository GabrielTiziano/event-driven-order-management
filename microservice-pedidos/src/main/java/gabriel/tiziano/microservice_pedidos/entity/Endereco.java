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
public class Endereco {

    @Column(name = "endereco_logradouro", length = 100)
    private String logradouro;

    @Column(name = "endereco_numero", length = 10)
    private String numero;

    @Column(name = "endereco_bairro", length = 100)
    private String bairro;

    @Column(name = "endereco_cidade", length = 100)
    private String cidade;

    @Column(name = "endereco_cep", length = 8)
    private String cep;
}