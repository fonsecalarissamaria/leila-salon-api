package com.salon.leila.agendamento;

import java.time.LocalDateTime;

public record DadosAtualizacaoAgendamento(
        LocalDateTime data,
        String servicos
) {}