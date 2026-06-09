const API_BASE_URL = "http://localhost:8080/api";

let patents = [];
let cart = [];
let currentUserRole = "VISITANTE";
let loggedUserId = null;

// =========================================================================
// CONTROLE DE SEÇÕES E LOGIN
// =========================================================================

function showSection(id) {
    document.querySelectorAll('.page-section')
        .forEach(s => s.classList.remove('active-section'));

    document.getElementById(id).classList.add('active-section');

    if (id === 'home') {
        // Aplica a regra de visualização ao mudar para a seção home
        gerenciarExibicaoPatentes();
    }
}

function showRegisterForm(role) {
    currentUserRole = role;
    document.getElementById('reg-title').innerText = "Cadastro de " + role;
    showSection('register-form');
}

function logout() {
    localStorage.removeItem('loggedUserId');
    localStorage.removeItem('currentUserRole');
    location.reload();
}

function loginAsNIT() {
    currentUserRole = "NIT";

    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-cart').classList.add('hidden');

    document.getElementById('nav-nit').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');
    document.getElementById('nav-profile').classList.remove('hidden');

    showSection('nit-panel');
    // NIT vê todas as patentes cadastradas
    renderPatents(patents);
}

function loginAsUser(role) {
    currentUserRole = role;

    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-nit').classList.add('hidden');

    document.getElementById('nav-cart').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');
    document.getElementById('nav-profile').classList.remove('hidden');

    showSection('home');
    // Usuários comuns veem apenas patentes disponíveis
    gerenciarExibicaoPatentes();
}

// Função auxiliar criada para centralizar o filtro de exibição
function gerenciarExibicaoPatentes() {
    if (currentUserRole === "NIT") {
        renderPatents(patents);
    } else {
        const disponiveis = patents.filter(p => {
            const statusStr = (typeof p.status === 'object' && p.status !== null) ? p.status.name : p.status;
            return statusStr === "DISPONIVEL";
        });
        renderPatents(disponiveis);
    }
}

// =========================================================================
// SINCRO_CARRINHO (Nova função de carregamento remoto)
// =========================================================================
async function carregarCarrinhoDoServidor(idUsuario) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${idUsuario}`);
        if (response.ok) {
            const carrinhoCompleto = await response.json();

            if (carrinhoCompleto && carrinhoCompleto.itens) {
                cart = carrinhoCompleto.itens.map(item => {
                    const idBuscado = item.idPatente;

                    return patents.find(p => (p.id === idBuscado || p.idPatente === idBuscado)) || {
                        id: idBuscado,
                        titulo: "Patente Adicionada"
                    };
                });
            } else {
                cart = [];
            }
            updateCartUI();
        }
    } catch (error) {
        console.error("Erro ao sincronizar o carrinho remoto:", error);
    }
}

// =========================================================================
// USUÁRIOS
// =========================================================================

// Cadastro
async function handleRegistration(event) {
    event.preventDefault();

    const nomeOuRazao = event.target[0].value;
    const documento = event.target[1].value;
    const email = document.getElementById('reg-email').value;
    const senha = event.target[3].value;

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
            const perfil = usuarioCriado.tipoPerfil || (currentUserRole === 'Pesquisador' ? 'PESQUISADOR' : (currentUserRole === 'NIT' ? 'NIT' : 'ORGANIZACAO'));

            localStorage.setItem('loggedUserId', loggedUserId);
            localStorage.setItem('currentUserRole', perfil);

            if (perfil !== 'NIT') {
                await carregarCarrinhoDoServidor(loggedUserId);
            }

            if (perfil === 'NIT') {
                loginAsNIT();
            } else {
                loginAsUser(perfil);
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

            localStorage.setItem('loggedUserId', loggedUserId);
            localStorage.setItem('currentUserRole', perfil);

            alert(`Bem-vindo, ${usuario.razaoSocial || usuario.nome || "Usuário"}!`);

            if (perfil !== 'NIT') {
                await carregarCarrinhoDoServidor(loggedUserId);
            }

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

// Buscar usuário por ID
async function obterUsuarioPorId(idUsuario) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/${idUsuario}`);

        if (!response.ok) {
            throw new Error("Usuário não encontrado.");
        }

        return await response.json();

    } catch (error) {
        console.error("Erro ao obter perfil do usuário:", error);
    }
}

// Abrir tela de edição
async function abrirTelaEdicao() {
    const usuario = await obterUsuarioPorId(loggedUserId);

    document.getElementById('edit-nome').value = usuario.nome || usuario.razaoSocial || '';
    document.getElementById('edit-documento').value = usuario.cpf || usuario.cnpj || '';
    document.getElementById('edit-email').value = usuario.email || '';
    document.getElementById('edit-telefone').value = usuario.telefone || '';
    document.getElementById('edit-endereco').value = usuario.endereco || '';

    const consultoriaDiv = document.getElementById('consultoria-container');

    if (currentUserRole === 'PESQUISADOR') {
        consultoriaDiv.style.display = 'block';
        document.getElementById('edit-consultoria').checked = usuario.disponibilidadeConsultoria;
    } else {
        consultoriaDiv.style.display = 'none';
    }

    showSection('edit-profile');
}

// Atualizar usuário
async function atualizarUsuarioLogado() {
    if (!loggedUserId) {
        alert("Erro: Nenhum usuário autenticado.");
        return;
    }

    const email = document.getElementById('edit-email').value;
    const telefone = document.getElementById('edit-telefone').value;
    const endereco = document.getElementById('edit-endereco').value;
    const nome = document.getElementById('edit-nome').value;
    // MODIFICAÇÃO: CPF/CNPJ não é mais lido do formulário para atualização

    let endpoint = "";
    let payloadDTO = { email, telefone, endereco };
    const role = currentUserRole.toUpperCase();

    if (role === 'PESQUISADOR') {
        endpoint = `/pesquisador/${loggedUserId}`;
        payloadDTO.nome = nome;
        // MODIFICAÇÃO: Removido envio do documento (CPF imutável)
        payloadDTO.disponibilidadeConsultoria = document.getElementById('edit-consultoria').checked;
    } else if (role === 'NIT') {
        endpoint = `/nit/${loggedUserId}`;
        payloadDTO.razaoSocial = nome;
        // MODIFICAÇÃO: Removido envio do documento (CNPJ imutável)
    } else if (role === 'ORGANIZAÇÃO INTERESSADA' || role === 'ORGANIZACAO') {
        endpoint = `/organizacao/${loggedUserId}`;
        payloadDTO.razaoSocial = nome;
        // MODIFICAÇÃO: Removido envio do documento (CNPJ imutável)
    } else {
        alert("Erro: Perfil de usuário desconhecido.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/usuarios${endpoint}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payloadDTO)
        });

        if (response.ok) {
            alert("Dados atualizados com sucesso!");
            showSection('home');
            return await response.json();
        } else {
            const erroTexto = await response.text();
            alert(`Falha ao atualizar dados: ${erroTexto}`);
        }
    } catch (error) {
        console.error("Erro na atualização:", error);
        alert("Erro de conexão com o servidor.");
    }
}

// Deletar usuário
async function deletarUsuarioLogado() {
    if (!loggedUserId || !confirm("Tem certeza que deseja desativar/deletar sua conta?")) {
        return;
    }

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

// Buscar NIT
async function buscarNitPorRazaoSocial(razaoSocial) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/nit/buscar?razaoSocial=${encodeURIComponent(razaoSocial)}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar NIT:", error);
    }
}

// Buscar pesquisador
async function buscarPesquisadorPorNome(nome) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/pesquisador/buscar?nome=${encodeURIComponent(nome)}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar pesquisador:", error);
    }
}

// Vincular associado ao NIT
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

// Desvincular associado
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
// PATENTES
// =========================================================================

// Listar patentes
async function fetchPatents() {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes`);

        if (!response.ok) {
            throw new Error("Erro ao buscar patentes.");
        }

        // Salva a lista bruta total para que o NIT acesse tudo na memória
        patents = await response.json();

        // CORREÇÃO: Chamada corrigida de 'gericaoExibicaoPatentes' para o nome correto
        gerenciarExibicaoPatentes();

    } catch (error) {
        console.error("Erro ao carregar vitrine:", error);
    }
}

// Publicar patente
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
        status: currentUserRole === "NIT"
            ? "DISPONIVEL"
            : "RASCUNHO",
        idsPesquisadoresAssociados: []
    };

    try {
        const response = await fetch(
            `${API_BASE_URL}/patentes/${loggedUserId}`,
            {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dto)
            }
        );

        if (response.status === 201) {

            alert("Patente publicada no banco de dados com sucesso!");

            document.getElementById('nit-panel')
                .querySelector('form')
                .reset();

            await fetchPatents();

            showSection('home');

        } else {
            alert("Erro ao publicar patente no servidor.");
        }

    } catch (error) {
        console.error("Erro ao enviar dados da patente:", error);
    }
}

// Buscar patente por ID
async function buscarPatentePorId(idPatente) {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar detalhes da patente:", error);
    }
}

async function atualizarDadosPatente(idPatente, patenteRequestDTO) {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patenteRequestDTO)
        });

        if (response.ok) {
            alert("Dados da patente atualizados.");
            await fetchPatents();
        }
    } catch (error) {
        console.error("Erro na atualização da patente:", error);
    }
}

async function alterarStatusPatente(idPatente, novoStatus) {
    if (!loggedUserId) return;
    try {
        const response = await fetch(`${API_BASE_URL}/patentes/${idPatente}/status?novoStatus=${novoStatus}`, {
            method: 'PATCH',
            headers: { 'X-Usuario-Id': loggedUserId.toString() }
        });
        if (response.ok) {
            alert(await response.text());
            await fetchPatents();
        }
    } catch (error) {
        console.error("Erro ao alterar status da patente:", error);
    }
}

async function deletarPatente(idPatente) {
    if (!confirm("Deseja realmente remover esta patente?")) return;
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

function baixarPdfPatente(idPatente) {
    window.open(`${API_BASE_URL}/patentes/${idPatente}/baixar-pdf`, '_blank');
}

// =========================================================================
// CARRINHO
// =========================================================================

async function addToCart(idPatente) {
    if (currentUserRole === "VISITANTE") {
        if (confirm("Você precisa estar logado para manifestar interesse. Deseja fazer login?")) {
            showSection('login-section');
        }
        return;
    }

    if (currentUserRole === "NIT") {
        alert("Gestores do NIT não realizam aquisições.");
        return;
    }

    const itemExistente = cart.find(item => (item.id === idPatente || item.idPatente === idPatente));

    if (itemExistente) {
        alert("Esta patente já foi adicionada.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPatente: idPatente })
        });

        if (response.status === 201) {
            const patenteParaAdicionar = patents.find(p => (p.id === idPatente || p.idPatente === idPatente));
            cart.push(patenteParaAdicionar || { id: idPatente, titulo: "Patente Adicionada" });
            updateCartUI();
            renderizarConteudoCarrinho();
            toggleCart();
            alert("Patente adicionada ao carrinho!");
        } else {
            const msgErro = await response.text();
            alert(`Erro: ${msgErro || "Não foi possível salvar o item."}`);
        }
    } catch (error) {
        console.error("Erro ao adicionar item:", error);
    }
}

async function removeFromCart(idPatente) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens/${idPatente}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            cart = cart.filter(item => (item.id !== idPatente && item.idPatente !== idPatente));
            updateCartUI();
            renderizarConteudoCarrinho();
        } else {
            alert("Erro ao remover o item.");
        }
    } catch (error) {
        console.error("Erro ao deletar item:", error);
    }
}

function renderizarConteudoCarrinho() {
    const container = document.getElementById('cart-items'); // Certifique-se que este ID existe no seu HTML
    if (!container) return;

    if (cart.length === 0) {
        container.innerHTML = "<p>Seu carrinho está vazio.</p>";
    } else {
        container.innerHTML = cart.map(item => `
        <div class="cart-item" style="border-bottom: 1px solid #eee; padding: 10px; display: flex; justify-content: space-between; align-items: center;">
            <p style="margin: 0;"><strong>${item.titulo}</strong></p>
            <button class="btn-remove" onclick="removeFromCart('${item.id || item.idPatente}')">
                Remover
            </button>
        </div>
    `).join('');
    }
}

// =========================================================================
// AQUISIÇÕES E RESTANTE DA UI
// =========================================================================

async function finalizarInteresse() {
    if (cart.length === 0) {
        alert("Seu carrinho está vazio!");
        return;
    }

    // 1. Armazena o carrinho no localStorage para que a página de checkout tenha acesso
    localStorage.setItem('checkoutItems', JSON.stringify(cart));

    // 2. Redireciona o usuário para a página de formulário de aquisição
    window.location.href = "checkout-patente.html";
}

async function criarAquisicaoDireta(aquisicaoRequestDTO) {
    try {
        const response = await fetch(`${API_BASE_URL}/aquisicoes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(aquisicaoRequestDTO)
        });
        if (response.status === 201) return await response.json();
    } catch (error) {
        console.error("Erro ao criar aquisição:", error);
    }
}

async function buscarAquisicaoPorId(idAquisicao) {
    try {
        const response = await fetch(`${API_BASE_URL}/aquisicoes/${idAquisicao}`);
        if (response.ok) return await response.json();
    } catch (error) {
        console.error("Erro ao buscar aquisição:", error);
    }
}

async function confirmarAquisicao() {
    const idUsuario = localStorage.getItem('loggedUserId');

    // A rota deve incluir /checkout/ + idUsuario
    const url = `${API_BASE_URL}/aquisicoes/checkout/${idUsuario}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.status === 201) {
            const mensagem = await response.text();
            alert(mensagem); // "Checkout finalizado com sucesso! ID da Transação: ..."
            localStorage.removeItem('checkoutItems');
            window.location.href = "index.html";
        } else {
            const erro = await response.text();
            alert("Erro: " + erro);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
    }
}

function toggleCart() {
    document.getElementById('cart-modal').classList.toggle('active');
}

function updateCartUI() {
    const cartCountElement = document.getElementById('cart-count');
    // Adicionando essa verificação de segurança:
    if (cartCountElement) {
        cartCountElement.innerText = cart.length;
    } else {
        console.warn("Elemento 'cart-count' não encontrado no DOM.");
    }
}

function renderPatents(listaPatentes) {
    const catalog = document.getElementById('patentCatalog');
    if (!catalog) return;

    if (!listaPatentes || listaPatentes.length === 0) {
        catalog.innerHTML = `<p style="grid-column:1/-1; text-align:center; color:#666;">Nenhuma patente disponível no momento.</p>`;
        return;
    }

    catalog.innerHTML = listaPatentes.map(p => {
        const statusAtual = (typeof p.status === 'object' && p.status !== null) ? p.status.name : p.status;

        let corStatus = '#007bff';
        let corTexto = 'white';
        if (statusAtual === 'DISPONIVEL') {
            corStatus = '#28a745';
        } else if (statusAtual === 'RASCUNHO') {
            corStatus = '#6c757d';
        } else if (statusAtual === 'REPROVADA' || statusAtual === 'EXCLUIDA') {
            corStatus = '#dc3545';
        } else if (statusAtual === 'EM_PROCESSO_DE_COMPRA') {
            corStatus = '#ffc107';
            corTexto = '#333';
        } else if (statusAtual === 'CEDIDA' || statusAtual === 'LICENCIADA') {
            corStatus = '#17a2b8';
        }


        return `
            <div class="patent-card">
                <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:10px; margin-bottom:10px;">
                    <span class="badge">${p.area || 'Geral'}</span>
                ${currentUserRole === 'NIT' || currentUserRole === 'ORGANIZACAO' || currentUserRole === 'ORGANIZAÇÃO INTERESSADA'
                ? `<span class="badge" style="background:${corStatus}; color:${corTexto}; text-align:center;">${statusAtual.replace(/_/g, ' ')}</span>`
                : ''
            }
                </div>
                <h3>${p.titulo}</h3>
                <p class="inventor"><strong>Inventores:</strong> ${p.pesquisadores || 'Não informado'}</p>
                <p class="description">${p.resumo}</p>
                <div style="margin-top:10px; display:flex; gap:5px;">
                    ${currentUserRole !== 'NIT' ? `
                        <button class="btn-add-cart" onclick="addToCart('${p.id || p.idPatente}')">
                            Manifestar Interesse
                        </button>
                    ` : `
                        <button class="btn-remove" style="background:#dc3545;" onclick="deletarPatente('${p.id || p.idPatente}')">
                            Excluir
                        </button>
                    `}
                    ${p.documento ? `
                        <button class="btn-add-cart" style="background:#6c757d;" onclick="baixarPdfPatente('${p.id || p.idPatente}')">
                            PDF
                        </button>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');
}

// =========================================================================
// INICIALIZAÇÃO E SESSÃO PERSISTENTE (Gerenciamento pós-refresh)
// =========================================================================

async function inicializarAplicacao() {
    await fetchPatents(); // 1. Garante que as patentes existam na memória

    const idSalvo = localStorage.getItem('loggedUserId');
    const perfilSalvo = localStorage.getItem('currentUserRole');

    if (idSalvo && perfilSalvo) {
        loggedUserId = idSalvo;
        currentUserRole = perfilSalvo;

        console.log(`Sessão restaurada: ${loggedUserId} (${currentUserRole})`);

        if (currentUserRole !== 'NIT') {
            // 2. Aguarda o carregamento do carrinho E renderiza
            await carregarCarrinhoDoServidor(loggedUserId);
            renderizarConteudoCarrinho(); // <--- Adicione esta linha!
            loginAsUser(currentUserRole);
        } else {
            loginAsNIT();
        }
    } else {
        currentUserRole = "VISITANTE";
        showSection('home');
    }
}

inicializarAplicacao();
