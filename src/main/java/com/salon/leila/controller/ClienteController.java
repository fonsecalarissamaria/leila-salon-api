package com.salon.leila.controller;

import com.salon.leila.cliente.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
public class ClienteController {

    @Autowired // injeção de dependência
    private ClienteRepository respository;

    @PostMapping
    @Transactional // transação ativa com o banco de dados
    public void cadastrar(@RequestBody @Valid DadosCadastroCliente dados){
        respository.save(new Cliente(dados));
    }

    @GetMapping
    public Page<DadosListagemCliente> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        return respository.findAllByAtivoTrue(paginacao).map(DadosListagemCliente::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid AtualizaDadosCliente dados){

        var cliente = respository.getReferenceById(dados.id());
        cliente.atualizarInformacoes(dados);

    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id){
        var cliente = respository.getReferenceById(id);
        cliente.excluir();
    }

    @GetMapping("/{id}")
    public Cliente detalhar(@PathVariable Long id) {
        return respository.findById(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    @GetMapping("/cpf/{cpf}")
    public Cliente buscarPorCpf(@PathVariable String cpf) {

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        return respository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
}
