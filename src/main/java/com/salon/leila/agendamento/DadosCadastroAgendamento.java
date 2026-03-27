package com.salon.leila.agendamento;

import java.time.LocalDateTime;

public record DadosCadastroAgendamento(
        String cpf,
        LocalDateTime data,
        String servicos
) {}
