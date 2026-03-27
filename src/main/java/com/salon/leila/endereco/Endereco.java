package com.salon.leila.endereco;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;
    private String numero;
    private String complemento;

    public Endereco(DadosEndereco dados){
        this.logradouro = dados.logradouro();
        this.bairro = dados.bairro();
        this.cep = dados.cep();
        this.uf = dados.uf();
        this.cidade = dados.cidade();
        this.numero = dados.numero();
        this.complemento = dados.complemento();
    }

    public void atualizarInfoEndereco(DadosEndereco dadosEndereco){
        if(dadosEndereco.logradouro() != null){
            this.logradouro = dadosEndereco.logradouro();
        }

        if(dadosEndereco.bairro() != null){
            this.bairro = dadosEndereco.bairro();
        }

        if(dadosEndereco.cep() != null){
            this.cep = dadosEndereco.cep();
        }

        if(dadosEndereco.uf() != null){
            this.uf = dadosEndereco.uf();
        }

        if(dadosEndereco.cidade() != null){
            this.cidade = dadosEndereco.cidade();
        }

        if(dadosEndereco.numero() != null){
            this.numero = dadosEndereco.numero();
        }

        if(dadosEndereco.complemento() != null){
            this.complemento = dadosEndereco.complemento();
        }
    }
}
