package com.salon.leila.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEndereco(
        @NotBlank
        String logradouro,

        @NotBlank
        String bairro,

        @NotBlank
        @Pattern(regexp = "\\d{8}") // cep tem 8 digitos: 12345-678
        String cep,

        @NotBlank
        String cidade,

        @NotBlank
        String uf,

        String numero,

        String complemento) {
}
