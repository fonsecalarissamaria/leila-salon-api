package com.salon.leila.cliente;

import com.salon.leila.endereco.DadosEndereco;
import jakarta.validation.constraints.NotNull;

public record AtualizaDadosCliente(
        @NotNull
        Long id,
        String nome,
        DadosEndereco endereco,
        String telefone){
}