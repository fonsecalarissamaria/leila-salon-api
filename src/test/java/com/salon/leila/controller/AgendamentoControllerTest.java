package com.salon.leila.controller;

import com.salon.leila.agendamento.Agendamento;
import com.salon.leila.agendamento.AgendamentoRepository;
import com.salon.leila.agendamento.DadosCadastroAgendamento;
import com.salon.leila.cliente.Cliente;
import com.salon.leila.cliente.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoControllerTest {

    @InjectMocks
    private AgendamentoController controller;

    @Mock
    private AgendamentoRepository repository;

    @Mock
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Deve cadastrar agendamento com sucesso quando cliente não tem horário na semana")
    void cadastrar_Cenario1() {
        String cpf = "11122233344";
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(5);
        DadosCadastroAgendamento dados = new DadosCadastroAgendamento(cpf, dataFutura, "Corte");

        Cliente clienteMock = mock(Cliente.class);
        when(clienteMock.getId()).thenReturn(1L);

        when(clienteRepository.findByCpf(cpf)).thenReturn(Optional.of(clienteMock));
        when(repository.findByClienteIdAndDataBetween(any(), any(), any())).thenReturn(new ArrayList<>());

        String resposta = controller.cadastrar(dados);

        assertEquals("Agendamento criado com sucesso", resposta);
        verify(repository, times(1)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Não deve permitir alteração com menos de 48 horas de antecedência")
    void atualizar_CenarioErro48Horas() {
        Long idAgendamento = 1L;

        LocalDateTime dataOriginal = LocalDateTime.now().plusHours(24); // agendamento pra daqui 24 horas - erro

        Agendamento agendamentoMock = new Agendamento();
        agendamentoMock.setId(idAgendamento);
        agendamentoMock.setData(dataOriginal);

        DadosCadastroAgendamento dadosNovos = new DadosCadastroAgendamento("111", LocalDateTime.now().plusDays(5), "Corte");

        when(repository.findById(idAgendamento)).thenReturn(Optional.of(agendamentoMock));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.atualizar(idAgendamento, dadosNovos);
        });

        assertEquals("Alteração só permitida com 2 dias de antecedência", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir alteração quando antecedência for maior que 48 horas")
    void atualizar_CenarioSucesso() {
        Long idAgendamento = 1L;
        LocalDateTime dataOriginal = LocalDateTime.now().plusDays(5);

        Agendamento agendamentoMock = new Agendamento();
        agendamentoMock.setId(idAgendamento);
        agendamentoMock.setData(dataOriginal);

        DadosCadastroAgendamento dadosNovos = new DadosCadastroAgendamento("111", LocalDateTime.now().plusDays(6), "Corte e Escova");

        when(repository.findById(idAgendamento)).thenReturn(Optional.of(agendamentoMock));

        controller.atualizar(idAgendamento, dadosNovos);

        assertEquals("Corte e Escova", agendamentoMock.getServicos());
        assertEquals(AgendamentoController.StatusAgendamento.PENDENTE, agendamentoMock.getStatus());
    }
}