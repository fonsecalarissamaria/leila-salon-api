package com.salon.leila.controller;

import com.salon.leila.agendamento.Agendamento;
import com.salon.leila.agendamento.AgendamentoRepository;
import com.salon.leila.agendamento.DadosCadastroAgendamento;
import com.salon.leila.cliente.Cliente;
import com.salon.leila.cliente.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    public enum StatusAgendamento {
        PENDENTE,
        CONFIRMADO,
        CANCELADO
    }

    @PostMapping
    @Transactional
    public String cadastrar(@RequestBody DadosCadastroAgendamento dados) {

        Cliente cliente = clienteRepository.findByCpf(dados.cpf())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        LocalDateTime data = dados.data();

        LocalDateTime inicioSemana = data.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime fimSemana = data.toLocalDate().with(DayOfWeek.SUNDAY).atTime(23,59,59);

        List<Agendamento> agendamentosSemana =
                repository.findByClienteIdAndDataBetween(cliente.getId(), inicioSemana, fimSemana);

        if (!agendamentosSemana.isEmpty()) {
            Agendamento primeiro = agendamentosSemana.get(0);
            return "Cliente já possui agendamento nesta semana. Sugestão: "
                    + primeiro.getData();
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setData(data);
        agendamento.setServicos(dados.servicos());
        agendamento.setAtivo(true);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        repository.save(agendamento);

        return "Agendamento criado com sucesso";
    }

    @PutMapping("/{id}")
    @Transactional
    public void atualizar(@PathVariable Long id, @RequestBody DadosCadastroAgendamento dados) {

        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        LocalDateTime agora = LocalDateTime.now();

        long horas = Duration.between(agora, agendamento.getData()).toHours();

        if (horas < 48) {
            throw new RuntimeException("Alteração só permitida com 2 dias de antecedência");
        }

        agendamento.setData(dados.data());
        agendamento.setServicos(dados.servicos());
        agendamento.setStatus(StatusAgendamento.PENDENTE);
    }

    @PutMapping("/admin/{id}")
    @Transactional
    public void atualizarAdmin(@PathVariable Long id, @RequestBody DadosCadastroAgendamento dados) {

        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        agendamento.setData(dados.data());
        agendamento.setServicos(dados.servicos());
    }

    @GetMapping
    public List<Agendamento> listarPorPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim
    ) {
        return repository.findByDataBetween(inicio, fim);
    }

    @GetMapping("/{id}")
    public Agendamento detalhar(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }

    @PutMapping("/confirmar/{id}")
    @Transactional
    public void confirmar(@PathVariable Long id) {

        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
    }

    @GetMapping("/cliente/{id}")
    public List<Agendamento> listarCliente(@PathVariable Long id) {
        return repository.findByClienteId(id);
    }

    @GetMapping("/pendentes")
    public Page<Agendamento> listarPendentes(
            @PageableDefault(size = 10, sort = {"data"}) Pageable paginacao
    ) {
        return repository.findByStatus(StatusAgendamento.PENDENTE, paginacao);
    }
}
