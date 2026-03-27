# Leila Salon API

Este projeto consiste em uma **API REST** para o gerenciamento de um salão de beleza. A solução engloba o controle de clientes, endereços e agendamentos, integrando regras de negócio e uma interface web para interação do usuário.

-----

## Sobre o Projeto

O sistema foi desenvolvido para atender às necessidades específicas da **Cabeleireira Leila**, permitindo que seus clientes realizem agendamentos online de forma personalizada.


### Principais Funcionalidades:

* **Agendamento de Serviços:** Possibilidade de agendar um ou mais serviços em uma única operação de forma prática.
* **Acesso Restrito (Gestão):** Área administrativa protegida por **login e senha**, garantindo que apenas a proprietária e funcionários autorizados acessem os dados gerenciais.
* **Regra de Alteração (48h):** Alterações via sistema permitidas apenas com até 2 dias de antecedência. Caso contrário, o sistema orienta o contato via telefone.
* **Gestão de Status:** Controle operacional para confirmação e gerenciamento do status de cada serviço solicitado.
* **Exclusão Lógica:** Registros não são apagados fisicamente do banco de dados, preservando o histórico de agendamentos através de um sistema de ativação/desativação.

---

## Tecnologias Utilizadas

### **Backend**

* **Java 21**: Linguagem de programação em sua versão estável mais recente (LTS).
* **Spring Boot 3.4.3**: Framework base para gerenciamento de dependências e produtividade.
* **Spring Data JPA**: Abstração para persistência de dados e comunicação eficiente com o banco.
* **Jakarta Validation**: Garantia de integridade dos dados (ex: `@NotBlank` e `@Pattern`).
* **Flyway**: Controle de versionamento do schema do banco de dados através de Migrations.
* **MySQL**: Banco de dados relacional utilizado para produção e containers.
* **H2 Database**: Banco de dados em memória para execução ágil de testes unitários.
* **JUnit 5 & Mockito**: Garantia de qualidade via testes automatizados e simulação de dependências.
* **Maven**: Automação do ciclo de vida e gerenciamento de build.

### **Frontend**

* **HTML5 & CSS3**: Estruturação semântica e estilização da interface.
* **JavaScript (Vanilla)**: Lógica de consumo da API via `Fetch API` e manipulação dinâmica do DOM.

### **Infraestrutura**

* **Docker & Docker Compose**: Containerização total da aplicação, garantindo que o sistema rode de forma idêntica em qualquer ambiente.

-----

## Como Rodar o Projeto

A execução é simplificada através do **Docker Compose**.

### **1. Pré-requisitos**

* Possuir o **Docker** e o **Docker Compose** instalados.
* Garantir que as portas **8080** (API) e **3306** (MySQL) estejam livres.

### **2. Execução**

No terminal, dentro da pasta raiz do projeto, execute:

```bash
docker-compose up -d --build
```

### **3. Acesso**

* **Sistema:** [http://localhost:8080](http://localhost:8080)
* **Acesso à Gestão:** Para acessar as funcionalidades operacionais e gerenciais do salão, utilize a senha: leila123
* **Banco de Dados (MySQL - Docker):** A aplicação utiliza um banco MySQL rodando em container Docker. Para acessar via terminal:

```bash
docker exec -it mysql_container mysql -u root -p
```

Senha: 123456

Após acessar:

```bash
USE leila_salon;
SHOW TABLES;
```

**Observação:** O backend se conecta ao banco utilizando o hostname interno do Docker (`mysql:3306`), através da rede criada automaticamente pelo Docker Compose.

-----

## Observações

### **Demonstração do Sistema**

Os prints e o vídeo de funcionamento estão disponíveis na pasta docs/ no diretório principal.

### **Arquitetura DTO**

Utilização de **Java Records** para representar os Data Transfer Objects, assegurando imutabilidade e um contrato de dados rígido entre cliente e servidor.

### **Persistência e Composição**

Uso da anotação `@Embeddable` na classe `Endereco`, permitindo que os dados de localização sejam organizados em uma classe separada, mas armazenados na mesma tabela de `Cliente`.

### **Regras de Negócio e Validação**

Implementação de filtros no backend que bloqueiam atualizações de agendamentos fora do prazo estipulado (48h), além de validações robustas de campos obrigatórios e formatos de dados (como CEP).

### **Testes Automatizados**

Presença de testes de **Repository** (persistência) e **Controller** (endpoints), garantindo que as mudanças no código não quebrem funcionalidades existentes.
