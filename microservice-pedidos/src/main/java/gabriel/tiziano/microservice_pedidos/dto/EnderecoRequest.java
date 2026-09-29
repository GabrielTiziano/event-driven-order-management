package gabriel.tiziano.microservice_pedidos.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @Size(max = 100) String logradouro,
        @Size(max = 10) String numero,
        @Size(max = 100) String bairro,
        @Size(max = 100) String cidade,
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos")
        String cep
) {
}