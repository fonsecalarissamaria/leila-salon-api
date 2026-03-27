const API = "http://localhost:8080";
let paginaAtual = 0;

// ---- Clientes (cadastro e gestao)

function cadastrarCliente() {
    const id = document.getElementById("clienteId") ? document.getElementById("clienteId").value : "";
    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const cpf = document.getElementById("cpf").value.replace(/\D/g, "");
    const telefone = document.getElementById("telefone").value.replace(/\D/g, "");

    const cep = document.getElementById("cep").value.replace(/\D/g, "");
    const logradouro = document.getElementById("logradouro").value;
    const numero = document.getElementById("numero").value;
    const complemento = document.getElementById("complemento").value;
    const bairro = document.getElementById("bairro").value;
    const cidade = document.getElementById("cidade").value;
    const uf = document.getElementById("uf").value;

    const dados = {
        nome: nome,
        email: email,
        cpf: cpf,
        telefone: telefone,
        endereco: {
            logradouro: logradouro,
            numero: numero,
            complemento: complemento,
            bairro: bairro,
            cep: cep,
            cidade: cidade,
            uf: uf
        }
    };

    const metodoHttp = id ? "PUT" : "POST";
    if (id) dados.id = id;

    fetch(API + "/clientes", {
        method: metodoHttp,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    })
    .then(res => {
        if(res.ok) {
            alert(id ? "Cadastro atualizado!" : "Cadastro realizado com sucesso!");
            if (document.getElementById("listaClientes")) listarClientes();
            limparFormularioCliente();
        } else {
            alert("Erro ao salvar. Verifique se preencheu todos os campos corretamente.");
        }
    })
    .catch(err => console.error("Erro na requisição:", err));
}

function listarClientes() {
    fetch(`${API}/clientes?page=${paginaAtual}&size=10`)
        .then(res => res.json())
        .then(data => {
            const lista = document.getElementById("listaClientes");
            if (!lista) return;

            lista.innerHTML = "";
            const clientes = data.content;

            clientes.forEach(cliente => {
                const li = document.createElement("li");
                li.innerHTML = `
                    <div class="client-data">
                        <span class="client-name">${cliente.nome}</span>
                        <span class="client-info">${cliente.email} | CPF: ${cliente.cpf}</span>
                    </div>
                    <div class="action-buttons">
                        <button class="btn-icon btn-delete" onclick="excluirCliente('${cliente.id}')" title="Excluir">🗑️</button>
                    </div>
                `;
                lista.appendChild(li);
            });
        })
        .catch(err => console.error("Erro ao listar clientes:", err));
}

function excluirCliente(id) {
    if (confirm("Tem certeza que deseja excluir este cadastro?")) {
        fetch(`${API}/clientes/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                alert("Cliente excluído.");
                listarClientes();
            }
        })
        .catch(err => console.error("Erro ao excluir:", err));
    }
}

function mudarPagina(direcao) {
    if (direcao === 'proxima') {
        paginaAtual++;
    } else if (direcao === 'anterior' && paginaAtual > 0) {
        paginaAtual--;
    }

    if (document.getElementById("listaClientes")) listarClientes();
    if (document.getElementById("listaPendentes")) carregarPendentes();
}

function limparFormularioCliente() {
    const inputs = document.querySelectorAll("input");
    inputs.forEach(input => input.value = "");
}

// ---- Agendamentos (agendar e histórico)

function cadastrarAgendamento() {
    const cpf = document.getElementById("cpf").value.replace(/\D/g, "");
    const data = document.getElementById("data").value;
    const servicos = document.getElementById("servicos").value;

    fetch(API + "/agendamentos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            cpf: cpf,
            data: data,
            servicos: servicos
        })
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        document.getElementById("cpf").value = "";
        document.getElementById("data").value = "";
        document.getElementById("servicos").value = "";
    })
    .catch(err => console.error("Erro na requisição:", err));
}

function filtrarAgendamentos() {
    const inicio = document.getElementById("filtroInicio").value;
    const fim = document.getElementById("filtroFim").value;

    if (!inicio || !fim) {
        alert("Por favor, selecione a data de início e a data de fim para buscar.");
        return;
    }

    fetch(`${API}/agendamentos?inicio=${inicio}&fim=${fim}`)
        .then(res => res.json())
        .then(data => {
            const lista = document.getElementById("listaAgendamentosHist");
            if(!lista) return;

            lista.innerHTML = "";

            if (data.length === 0) {
                lista.innerHTML = "<p class='empty-message'>Nenhum agendamento encontrado neste período.</p>";
                return;
            }

            data.forEach(a => {
                const dataObj = new Date(a.data);
                const diaMes = dataObj.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
                const hora = dataObj.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

                const li = document.createElement("li");
                li.innerHTML = `
                    <div class="agenda-time">
                        ${diaMes}<br>${hora}
                    </div>
                    <div class="agenda-details">
                        <span class="client-name">${a.cliente.nome} (ID: ${a.cliente.id})</span>
                        <span class="service-type">Telefone: ${a.cliente.telefone || "Não informado"}</span>
                        <span class="service-type">Serviços: ${a.servicos}</span>
                        <br>
                        <input type="datetime-local" value="${a.data}" id="data-${a.id}" style="margin-bottom: 5px;">
                        <input type="text" value="${a.servicos}" id="servicos-${a.id}">
                        <button class="btn-primary" onclick="editarAgendamento(${a.id})" style="margin-top: 5px; padding: 5px; cursor: pointer;">Atualizar</button>
                    </div>
                `;
                lista.appendChild(li);
            });
        })
        .catch(err => console.error("Erro ao listar agendamentos:", err));
}

function editarAgendamento(id) {
    const novaData = document.getElementById(`data-${id}`).value;
    const novosServicos = document.getElementById(`servicos-${id}`).value;

    fetch(`${API}/agendamentos/admin/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            data: novaData,
            servicos: novosServicos
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Erro ao atualizar");
        alert("Agendamento atualizado com sucesso!");
        filtrarAgendamentos();
    })
    .catch(err => {
        alert("Erro ao atualizar agendamento");
        console.error(err);
    });
}

// ---- Agendamentos pendentes (gestão)

function carregarPendentes() {
    fetch(`${API}/agendamentos/pendentes?page=${paginaAtual}&size=10`)
        .then(res => res.json())
        .then(data => {
            const lista = document.getElementById("listaPendentes");
            if(!lista) return;

            lista.innerHTML = "";

            data.content.forEach(a => {
                const dataObj = new Date(a.data);
                const diaMes = dataObj.toLocaleDateString('pt-BR');
                const hora = dataObj.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

                const li = document.createElement("li");
                li.innerHTML = `
                    <div class="agenda-time">
                        ${diaMes}<br>${hora}
                    </div>
                    <div class="agenda-details">
                        <span class="client-name">${a.cliente.nome}</span>
                        <span class="service-type">Tel: ${a.cliente.telefone || 'Sem telefone'}</span>
                        <span class="service-type">Serviços: ${a.servicos}</span>
                        <button class="btn-primary" onclick="confirmarAgendamento(${a.id})" style="margin-top: 8px; padding: 8px; cursor: pointer;">Confirmar</button>
                    </div>
                `;
                lista.appendChild(li);
            });
        })
        .catch(err => console.error("Erro ao carregar pendentes:", err));
}

function confirmarAgendamento(id) {
    fetch(`${API}/agendamentos/confirmar/${id}`, { method: "PUT" })
        .then(res => {
            if(res.ok) {
                alert("Agendamento confirmado com sucesso!");
                carregarPendentes();
            } else {
                alert("Erro ao confirmar agendamento.");
            }
        })
        .catch(err => console.error(err));
}

// ---- Perfil da cliente

function buscarDadosCliente() {
    const cpf = document.getElementById("cpfBuscaCliente").value.replace(/\D/g, "");

    if (!cpf) {
        alert("Por favor, digite seu CPF.");
        return;
    }

    fetch(`${API}/clientes/cpf/${cpf}`)
        .then(res => {
            if (!res.ok) throw new Error("Cliente não encontrado");
            return res.json();
        })
        .then(cliente => {
            document.getElementById("clienteIdPerfil").value = cliente.id;

            if (document.getElementById("nome")) document.getElementById("nome").value = cliente.nome || "";
            if (document.getElementById("telefone")) document.getElementById("telefone").value = cliente.telefone || "";

            if (cliente.endereco) {
                if (document.getElementById("cep")) document.getElementById("cep").value = cliente.endereco.cep || "";
                if (document.getElementById("logradouro")) document.getElementById("logradouro").value = cliente.endereco.logradouro || "";
                if (document.getElementById("numero")) document.getElementById("numero").value = cliente.endereco.numero || "";
                if (document.getElementById("complemento")) document.getElementById("complemento").value = cliente.endereco.complemento || "";
                if (document.getElementById("bairro")) document.getElementById("bairro").value = cliente.endereco.bairro || "";
                if (document.getElementById("cidade")) document.getElementById("cidade").value = cliente.endereco.cidade || "";
                if (document.getElementById("uf")) document.getElementById("uf").value = cliente.endereco.uf || "";
            }

            document.getElementById("secaoLogin").style.display = "none";
            document.getElementById("secaoPerfil").style.display = "block";
            document.getElementById("mensagemBemVindo").innerText = `Bem-vinda, ${cliente.nome}!`;

            buscarMeusAgendamentos(cliente.id);
        })
        .catch(err => {
            alert("Cliente não encontrado. Verifique se o CPF está correto.");
            console.error(err);
        });
}

function buscarMeusAgendamentos(clienteId) {
    fetch(`${API}/agendamentos/cliente/${clienteId}`)
        .then(res => res.json())
        .then(data => {
            const lista = document.getElementById("listaMeusAgendamentos");
            if(!lista) return;

            lista.innerHTML = "";

            if (data.length === 0) {
                lista.innerHTML = "<p style='color: var(--text-light); text-align: center;'>Você não possui agendamentos cadastrados.</p>";
                return;
            }

            data.forEach(a => {
                const div = document.createElement("div");
                div.className = "agendamento-item";

                const dataInput = a.data.substring(0, 16);

                let classeDaTag = a.status === 'CONFIRMADO' ? 'tag-confirmado' : 'tag-pendente';
                let textoDaTag = a.status === 'CONFIRMADO' ? 'Confirmado' : 'Pendente';

                div.innerHTML = `
                    <span class="tag-status ${classeDaTag}">⏳ ${textoDaTag}</span>

                    <div class="form-group" style="margin-top: 0.5rem;">
                        <label>Data e Horário</label>
                        <input type="datetime-local" id="data_agenda_${a.id}" value="${dataInput}">
                    </div>

                    <div class="form-group">
                        <label>Serviços</label>
                        <input type="text" id="servicos_agenda_${a.id}" value="${a.servicos}">
                    </div>

                    <button class="btn-primary" onclick="atualizarMeuAgendamento(${a.id}, ${clienteId})" style="margin-top:0.5rem; padding: 0.6rem;">Atualizar Dados do Agendamento</button>
                `;
                lista.appendChild(div);
            });
        })
        .catch(err => console.error("Erro ao buscar seus agendamentos:", err));
}

function atualizarMeuCadastro() {
    const id = document.getElementById("clienteIdPerfil").value;

    const dados = {
        id: id,
        nome: document.getElementById("nome").value,
        telefone: document.getElementById("telefone").value.replace(/\D/g, ""),
        endereco: {
            cep: document.getElementById("cep").value.replace(/\D/g, ""),
            logradouro: document.getElementById("logradouro").value,
            numero: document.getElementById("numero").value,
            complemento: document.getElementById("complemento").value,
            bairro: document.getElementById("bairro").value,
            cidade: document.getElementById("cidade").value,
            uf: document.getElementById("uf").value
        }
    };

    fetch(`${API}/clientes`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dados)
    })
    .then(res => {
        if(res.ok) alert("Seus dados foram atualizados com sucesso!");
        else alert("Erro ao atualizar dados. Verifique os campos.");
    })
    .catch(err => console.error(err));
}

function atualizarMeuAgendamento(idAgendamento, clienteId) {
    const novaData = document.getElementById(`data_agenda_${idAgendamento}`).value;
    const novosServicos = document.getElementById(`servicos_agenda_${idAgendamento}`).value;

    fetch(`${API}/agendamentos/${idAgendamento}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            clienteId: clienteId,
            data: novaData,
            servicos: novosServicos
        })
    })
    .then(res => {
        if (res.ok) {
            alert("Horário reagendado com sucesso!");
        } else {
            alert("Atenção: A alteração só pode ser feita com 48 horas de antecedência. Em caso de imprevistos, ligue para o salão.");
            buscarMeusAgendamentos(clienteId);
        }
    })
    .catch(err => {
        console.error("Erro ao reagendar:", err);
        alert("Ocorreu um erro no servidor.");
    });
}


// ---- Desempenho semanal (gestão)

function carregarDesempenhoSemanal() {
    const hoje = new Date();
    const diaSemana = hoje.getDay();

    const diffSegunda = hoje.getDate() - diaSemana + (diaSemana === 0 ? -6 : 1);

    const segunda = new Date(hoje.setDate(diffSegunda));
    segunda.setHours(0, 0, 0, 0);

    const domingo = new Date(segunda.getTime());
    domingo.setDate(segunda.getDate() + 6);
    domingo.setHours(23, 59, 59, 999);

    const strSegunda = segunda.toLocaleDateString('pt-BR');
    const strDomingo = domingo.toLocaleDateString('pt-BR');
    document.getElementById("textoPeriodo").innerText = `Analisando a semana atual: ${strSegunda} até ${strDomingo}`;

    const inicioStr = segunda.toISOString().substring(0, 19);
    const fimStr = domingo.toISOString().substring(0, 19);

    fetch(`${API}/agendamentos?inicio=${inicioStr}&fim=${fimStr}`)
        .then(res => res.json())
        .then(data => {
            const total = data.length;
            const confirmados = data.filter(a => a.status === 'CONFIRMADO').length;

            fetch(`${API}/agendamentos/pendentes?page=0&size=100`)
            .then(resPendente => resPendente.json())
            .then(dataPendente => {
                const pendentesNoTotal = dataPendente.content.length;

                document.getElementById("totalSemana").innerText = total + pendentesNoTotal;
                document.getElementById("totalConfirmados").innerText = confirmados || total;
                document.getElementById("totalPendentes").innerText = pendentesNoTotal;
            });
        })
        .catch(err => console.error("Erro ao carregar desempenho:", err));
}

// ---- Navegação do painel de gestão

function mudarAbaGestao(abaId) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    event.target.classList.add('active');
    document.getElementById('aba-' + abaId).classList.add('active');
}

function sairDaGestao() {
    sessionStorage.removeItem("leilaLogada");
    window.location.href = "index.html";
}


// ---- Inicialização

document.addEventListener("DOMContentLoaded", () => {

    if (document.getElementById("telaDesempenho")) {
        if (sessionStorage.getItem("leilaLogada") !== "sim") {
            alert("Sua sessão expirou ou você não fez login.");
            window.location.href = "login-gestao.html";
            return;
        }
    }

    if (document.getElementById("listaClientes")) listarClientes();
    if (document.getElementById("listaPendentes")) carregarPendentes();
    if (document.getElementById("telaDesempenho")) carregarDesempenhoSemanal();
});