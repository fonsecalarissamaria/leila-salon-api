package com.salon.leila.cliente;

import com.salon.leila.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record DadosCadastroCliente(
        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @NotNull
        @Valid
        DadosEndereco endereco,

        @NotBlank
        @Pattern(regexp = "\\d{10,11}") // telefone fixo ou celular (10 a 11 digitos
        String telefone,

        @NotBlank
        String cpf) {
}
