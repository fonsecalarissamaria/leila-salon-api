package com.salon.leila.agendamento;

import com.salon.leila.controller.AgendamentoController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByDataBetween(
            LocalDateTime inicio,
            LocalDateTime fim
    );

    List<Agendamento> findByClienteIdAndDataBetween(
            Long clienteId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    List<Agendamento> findByClienteIdAndStatus(
            Long clienteId,
            AgendamentoController.StatusAgendamento status
    );

    Page<Agendamento> findByStatus(
            AgendamentoController.StatusAgendamento status,
            Pageable pageable
    );

    List<Agendamento> findByClienteId(Long clienteId);

}