const API_BASE_URL = "http://localhost:8080/api";
let patents = [];
let cart = [];
let currentUserRole = "VISITANTE";
let loggedUserId = null;


function showSection(id) {
    document.querySelectorAll('.page-section').forEach(s => s.classList.remove('active-section'));
    document.getElementById(id).classList.add('active-section');
    if (id === 'home') renderPatents(patents);
}

function showRegisterForm(role) {
    currentUserRole = role;
    document.getElementById('reg-title').innerText = "Cadastro de " + role;
    showSection('register-form');
}

function logout() {
    location.reload();
}


function loginAsNIT() {
    currentUserRole = "NIT";
    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-cart').classList.add('hidden');

    document.getElementById('nav-nit').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');

    showSection('nit-panel');
    renderPatents(patents);
}

function loginAsUser(role) {
    currentUserRole = role;
    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-nit').classList.add('hidden');

    document.getElementById('nav-cart').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');

    showSection('home');
    renderPatents(patents);
}

// =========================================================================
// REQUISITIONS: USUARIOS (UsuarioController)
// =========================================================================

// Cadastro de Usuários
async function handleRegistration(event) {
    event.preventDefault();

    const nomeOuRazao = event.target[0].value;
    const documento   = event.target[1].value;
    const email       = document.getElementById('reg-email').value;
    const senha       = event.target[3].value;

    let endpointFinal = "";
    let payload = { email, senha };

    if (currentUserRole === 'Pesquisador') {
        endpointFinal = "/pesquisador";
        payload.nome = nomeOuRazao;
        payload.cpf = documento;
    } else if (currentUserRole === 'Organização Interessada') {
        endpointFinal = "/organizacao";
        payload.razaoSocial = nomeOuRazao;
        payload.cnpj = documento;
    } else if (currentUserRole === 'NIT') {
        endpointFinal = "/nit";
        payload.razaoSocial = nomeOuRazao;
        payload.cnpj = documento;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios${endpointFinal}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.status === 201) {
            const usuarioCriado = await response.json();
            alert(`${currentUserRole} cadastrado com sucesso!`);

            loggedUserId = usuarioCriado.idUsuario;

            if (currentUserRole === 'NIT') {
                loginAsNIT();
            } else if (currentUserRole === 'Pesquisador') {
                loginAsUser('PESQUISADOR');
            } else {
                loginAsUser('ORGANIZACAO');
            }
        } else {
            const erroTexto = await response.text();
            alert(`Falha no cadastro: ${erroTexto || 'Dados inválidos.'}`);
        }
    } catch (error) {
        console.error("Erro na requisição de cadastro:", error);
        alert("Erro ao conectar com o servidor.");
    }
}

// Login
async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('login-email').value;
    const senha = event.target[1].value;

    const loginPayload = { email, senha };

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginPayload)
        });

        if (response.ok) {
            const usuario = await response.json();
            loggedUserId = usuario.idUsuario;
            const perfil = usuario.tipoPerfil;

            alert(`Bem-vindo, ${usuario.razaoSocial || usuario.nome || "Usuário"}!`);

            if (perfil === 'NIT') {
                loginAsNIT();
            } else {
                loginAsUser(perfil);
            }
        } else {
            const erroTexto = await response.text();
            alert(`Falha na autenticação: ${erroTexto}`);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Não foi possível conectar ao servidor.");
    }
}

// Obter Usuário por ID
async function obterUsuarioPorId(idUsuario) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${idUsuario}`);
        if (!response.ok) throw new Error("Usuário não encontrado.");
        return await response.json();
    } catch (error) {
        console.error("Erro ao obter perfil do usuário:", error);
    }
}

// Atualizar Usuários na página de Conta (Progressivo com telefone, endereço e consultoria)
async function atualizarUsuarioLogado() {
    if (!loggedUserId) {
        alert("Erro: Nenhum usuário autenticado.");
        return;
    }

    // Captura os novos campos secundários presentes na página de conta
    const email    = document.getElementById('edit-email').value;
    const telefone = document.getElementById('edit-telefone').value;
    const endereco = document.getElementById('edit-endereco').value;

    let endpoint = "";
    let payloadDTO = { email, telefone, endereco };

    // Injeta os dados específicos e polimórficos de cada perfil na carga útil
    if (currentUserRole === 'PESQUISADOR') {
        endpoint = `/pesquisador/${loggedUserId}`;
        payloadDTO.nome = document.getElementById('edit-nome').value;
        payloadDTO.cpf  = document.getElementById('edit-documento').value;
        // Captura o booleano do checkbox de consultoria do pesquisador
        payloadDTO.disponibilidadeConsultoria = document.getElementById('edit-consultoria').checked;

    } else if (currentUserRole === 'NIT') {
        endpoint = `/nit/${loggedUserId}`;
        payloadDTO.razaoSocial = document.getElementById('edit-nome').value;
        payloadDTO.cnpj        = document.getElementById('edit-documento').value;

    } else if (currentUserRole === 'ORGANIZACAO') {
        endpoint = `/organizacao/${loggedUserId}`;
        payloadDTO.razaoSocial = document.getElementById('edit-nome').value;
        payloadDTO.cnpj        = document.getElementById('edit-documento').value;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios${endpoint}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payloadDTO)
        });

        if (response.ok) {
            alert("Dados cadastrais atualizados com sucesso!");
            return await response.json();
        } else {
            const erroTexto = await response.text();
            alert(`Falha ao atualizar dados: ${erroTexto}`);
        }
    } catch (error) {
        console.error("Erro ao atualizar cadastro:", error);
        alert("Erro de conexão ao salvar modificações.");
    }
}

// Excluir Usuário Conta
async function deletarUsuarioLogado() {
    if (!loggedUserId || !confirm("Tem certeza que deseja desativar/deletar sua conta?")) return;

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${loggedUserId}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            alert("Sua conta foi excluída com sucesso.");
            logout();
        }
    } catch (error) {
        console.error("Erro ao deletar usuário:", error);
    }
}

// Buscar NIT por Razão Social
async function buscarNitPorRazaoSocial(razaoSocial) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/nit/buscar?razaoSocial=${encodeURIComponent(razaoSocial)}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar NIT:", error);
    }
}

// Buscar Pesquisador por Nome
async function buscarPesquisadorPorNome(nome) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/pesquisador/buscar?nome=${encodeURIComponent(nome)}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar pesquisador:", error);
    }
}

// Adicionar Vínculo de Usuário Associado ao NIT
async function vincularUsuarioAoNit(idUsuarioParaVincular) {
    if (currentUserRole !== 'NIT' || !loggedUserId) return;
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${loggedUserId}/associados`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(idUsuarioParaVincular)
        });
        if (response.status === 201) alert(await response.text());
    } catch (error) {
        console.error("Erro ao vincular associado:", error);
    }
}

// Remover Vínculo de Usuário Associado do NIT
async function desvincularUsuarioDoNit(idUsuarioAssociado) {
    if (currentUserRole !== 'NIT' || !loggedUserId) return;
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${loggedUserId}/associados/${idUsuarioAssociado}`, {
            method: 'DELETE'
        });
        if (response.ok) alert(await response.text());
    } catch (error) {
        console.error("Erro ao desvincular associado:", error);
    }
}

// =========================================================================
// REQUISITIONS: PATENTES (PatenteController)
// =========================================================================

// Listar Patentes
async function fetchPatents() {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes`);
        if (!response.ok) throw new Error("Erro ao buscar patentes.");
        patents = await response.json();
        renderPatents(patents);
    } catch (error) {
        console.error("Erro ao carregar vitrine:", error);
    }
}

// Publicar patente pelo NIT
async function handlePatentSubmit(event) {
    event.preventDefault();

    if (!loggedUserId) {
        alert("Erro: ID do usuário gestor não identificado.");
        return;
    }

    const dto = {
        titulo: document.getElementById('p-title').value,
        numDeposito: "BR" + Math.floor(100000 + Math.random() * 900000),
        resumo: document.getElementById('p-desc').value,
        area: document.getElementById('p-area').value,
        valor: 0.0,
        pesquisadores: document.getElementById('p-inv').value,
        documento: null,
        idsPesquisadoresAssociados: []
    };

    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${loggedUserId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.status === 201) {
            alert("Patente publicada no banco de dados com sucesso!");
            document.getElementById('nit-panel').querySelector('form').reset();
            await fetchPatents();
            showSection('home');
        } else {
            alert("Erro ao publicar patente no servidor.");
        }
    } catch (error) {
        console.error("Erro ao enviar dados da patente:", error);
    }
}

// Buscar Patente por ID específico
async function buscarPatentePorId(idPatente) {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar detalhes da patente:", error);
    }
}

// Atualizar Patente Existente
async function atualizarDadosPatente(idPatente, patenteRequestDTO) {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patenteRequestDTO)
        });
        if (response.ok) {
            alert("Dados da patente updated.");
            await fetchPatents();
        }
    } catch (error) {
        console.error("Erro na atualização da patente:", error);
    }
}

// Alterar Status da Patente (Fluxo de validação interna de negócio)
async function alterarStatusPatente(idPatente, novoStatus) {
    if (!loggedUserId) return;
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}/status?novoStatus=${novoStatus}`, {
            method: 'PATCH',
            headers: {
                'X-Usuario-Id': loggedUserId.toString()
            }
        });
        if (response.ok) {
            alert(await response.text());
            await fetchPatents();
        }
    } catch (error) {
        console.error("Erro ao alterar status da patente:", error);
    }
}

// Deletar Patente do Repositório
async function deletarPatente(idPatente) {
    if (!confirm("Deseja realmente remover esta patente de forma permanente?")) return;
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}`, {
            method: 'DELETE'
        });
        if (response.status === 204 || response.status === 200) {
            alert("Patente removida com sucesso.");
            await fetchPatents();
        }
    } catch (error) {
        console.error("Erro ao excluir patente:", error);
    }
}

// Baixar Documentação Técnica / PDF da Patente
function baixarPdfPatente(idPatente) {
    window.open(`${API_BASE_URL}/patentes/${idPatente}/baixar-pdf`, '_blank');
}

// =========================================================================
// REQUISITIONS: CARRINHO (CarrinhoController)
// =========================================================================

// Adicionar item ao Carrinho Remoto
async function addToCart(idPatente) {
    if (currentUserRole === "VISITANTE") {
        alert("Atenção: Você precisa estar logado para adicionar patentes ao carrinho.");
        showSection('login-section');
        return;
    }

    if (currentUserRole === "NIT") {
        alert("Gestores do NIT não realizam aquisições.");
        return;
    }

    const itemExistente = cart.find(item => item.id === idPatente);
    if (itemExistente) {
        alert("Esta patente já foi adicionada ao seu carrinho.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(idPatente)
        });

        if (response.status === 201) {
            const patenteParaAdicionar = patents.find(p => p.id === idPatente);
            cart.push(patenteParaAdicionar);
            updateCartUI();
            toggleCart();
        } else {
            alert("Não foi possível salvar o item no seu carrinho no servidor.");
        }
    } catch (error) {
        console.error("Erro ao adicionar item ao carrinho remoto:", error);
    }
}

// Remover item do Carrinho Remoto
async function removeFromCart(idPatente) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens/${idPatente}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            cart = cart.filter(item => item.id !== idPatente);
            updateCartUI();
        } else {
            alert("Erro ao remover o item do servidor.");
        }
    } catch (error) {
        console.error("Erro ao deletar item do carrinho remoto:", error);
    }
}

// =========================================================================
// REQUISITIONS: AQUISICOES & CHECKOUT (AquisicaoController)
// =========================================================================

// Finalizar Checkout de Interesse das patentes alocadas no carrinho
async function finalizarInteresse() {
    if (cart.length === 0) {
        alert("Seu carrinho está vazio! Adicione patentes antes de finalizar.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/aquisicoes/checkout/${loggedUserId}`, {
            method: 'POST'
        });

        if (response.status === 201) {
            const mensagemSucesso = await response.text();
            alert(mensagemSucesso);

            cart = [];
            updateCartUI();
            toggleCart();
        } else {
            alert("Erro ao finalizar o checkout da aquisição.");
        }
    } catch (error) {
        console.error("Erro no processamento do checkout:", error);
    }
}

// Forçar a criação de uma aquisição estruturada direta via DTO externo
async function criarAquisicaoDireta(aquisicaoRequestDTO) {
    try {
        const response = await fetch(`${API_BASE_URL}/aquisicoes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(aquisicaoRequestDTO)
        });
        if (response.status === 201) {
            return await response.json();
        }
    } catch (error) {
        console.error("Erro ao criar aquisição externa direta:", error);
    }
}

// Buscar Histórico de Transação / Detalhes de Aquisição por ID
async function buscarAquisicaoPorId(idAquisicao) {
    try {
        const response = await fetch(`${API_BASE_URL}/aquisicoes/${idAquisicao}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao resgatar dados da aquisição:", error);
    }
}

// =========================================================================
// RENDERIZAÇÃO E INTERFACE GRÁFICA LOCAL (UI)
// =========================================================================

function toggleCart() {
    document.getElementById('cart-modal').classList.toggle('active');
}

function updateCartUI() {
    const itemsContainer = document.getElementById('cart-items');
    const countElement = document.getElementById('cart-count');

    countElement.innerText = cart.length;

    if (cart.length === 0) {
        itemsContainer.innerHTML = "<p style='color:#888; padding: 10px;'>Seu carrinho está vazio.</p>";
    } else {
        itemsContainer.innerHTML = cart.map(item => `
            <div class="cart-item">
                <span>${item.titulo}</span>
                <button class="btn-remove" onclick="removeFromCart('${item.id}')">Remover</button>
            </div>
        `).join('');
    }
}

function renderPatents(listaPatentes) {
    const catalog = document.getElementById('patentCatalog');
    if (!catalog) return;

    if (!listaPatentes || listaPatentes.length === 0) {
        catalog.innerHTML = "<p style='grid-column: 1/-1; text-align:center; color:#666;'>Nenhuma patente cadastrada no momento.</p>";
        return;
    }

    catalog.innerHTML = listaPatentes.map(p => `
        <div class="patent-card">
            <span class="badge">${p.area || 'Geral'}</span>
            <h3>${p.titulo}</h3>
            <p class="inventor"><strong>Inventores:</strong> ${p.pesquisadores || 'Não informado'}</p>
            <p class="description">${p.resumo}</p>
            <div style="margin-top:10px; display:flex; gap:5px;">
               ${currentUserRole !== 'NIT' ? `
                    <button class="btn-add-cart" onclick="addToCart('${p.id}')">
                        Manifestar Interesse
                    </button>
                ` : `
                    <button class="btn-remove" style="background:#dc3545;" onclick="deletarPatente('${p.id}')">
                        Excluir
                    </button>
                `}
                ${p.documento ? `
                    <button class="btn-add-cart" style="background:#6c757d;" onclick="baixarPdfPatente('${p.id}')">
                        PDF
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Inicializa a aplicação buscando dados do Java
fetchPatents();