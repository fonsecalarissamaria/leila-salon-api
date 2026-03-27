package com.salon.leila.agendamento;

import com.salon.leila.cliente.Cliente;
import com.salon.leila.controller.AgendamentoController;
import com.salon.leila.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AgendamentoRepositoryTest {

    @Autowired
    private AgendamentoRepository repository;

    @Autowired
    private TestEntityManager em;

    private Cliente criarClienteCompleto() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Eduarda");
        cliente.setEmail("maria@exemplo.com");
        cliente.setCpf("43677722893");
        cliente.setTelefone("1434250700");
        cliente.setAtivo(true);

        Endereco endereco = new Endereco();

        endereco.setLogradouro("Rua das Flores");
        endereco.setBairro("Centro");
        endereco.setCep("12345678");
        endereco.setCidade("Marília");
        endereco.setUf("SP");
        endereco.setNumero("100");
        endereco.setComplemento("Apto 1");

        cliente.setEndereco(endereco);

        return cliente;
    }

    @Test
    @DisplayName("Deve retornar apenas os agendamentos de um cliente específico")
    void findByClienteId_Cenario1() {
        Cliente cliente = criarClienteCompleto();
        em.persist(cliente);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setData(LocalDateTime.now().plusDays(1));
        agendamento.setServicos("Manicure");
        agendamento.setStatus(AgendamentoController.StatusAgendamento.CONFIRMADO);
        agendamento.setAtivo(true);
        em.persist(agendamento);

        List<Agendamento> resultado = repository.findByClienteId(cliente.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}