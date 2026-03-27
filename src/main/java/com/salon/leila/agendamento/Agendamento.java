package com.salon.leila.agendamento;

import com.salon.leila.cliente.Cliente;
import com.salon.leila.controller.AgendamentoController;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;

    private String servicos;

    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    private AgendamentoController.StatusAgendamento status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}