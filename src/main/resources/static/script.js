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

// Requisições

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

            // Captura o idUsuario salvo no banco
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

// Publicar patente pelo NIT
async function handlePatentSubmit(event) {
    event.preventDefault();

    if (!loggedUserId) {
        alert("Erro: ID do usuário gestor não identificado.");
        return;
    }

    const dto = {
        idTitular: loggedUserId,
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
            await fetchPatents(); // Atualiza a lista da Home
            showSection('home');
        } else {
            alert("Erro ao publicar patente no servidor.");
        }
    } catch (error) {
        console.error("Erro ao enviar dados da patente:", error);
    }
}

// 5. ADICIONAR AO CARRINHO PERSISTIDO (POST /api/carrinho/{idUsuario}/itens)
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

    const itemExistente = cart.find(item => item.idPatente === idPatente);
    if (itemExistente) {
        alert("Esta patente já foi adicionada ao seu carrinho.");
        return;
    }

    try {
        // Faz a requisição para salvar no banco de dados de acordo com o seu CarrinhoController
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(idPatente) // Envia o UUID da patente direto no RequestBody
        });

        if (response.status === 201) {
            const patenteParaAdicionar = patents.find(p => p.idPatente === idPatente);
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

// 6. REMOVER DO CARRINHO PERSISTIDO (DELETE /api/carrinho/{idUsuario}/itens/{idPatente})
async function removeFromCart(idPatente) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrinho/${loggedUserId}/itens/${idPatente}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            cart = cart.filter(item => item.idPatente !== idPatente);
            updateCartUI();
        } else {
            alert("Erro ao remover o item do servidor.");
        }
    } catch (error) {
        console.error("Erro ao deletar item do carrinho remoto:", error);
    }
}

// 7. FINALIZAR CHECKOUT / INTERESSE (POST /api/aquisicoes/checkout/{idUsuario})
async function finalizarInteresse() {
    if (cart.length === 0) {
        alert("Seu carrinho está vazio! Adicione patentes antes de finalizar.");
        return;
    }

    try {
        // Dispara para o seu AquisicaoController enviando o idUsuario na URL
        const response = await fetch(`${API_BASE_URL}/aquisicoes/checkout/${loggedUserId}`, {
            method: 'POST'
        });

        if (response.status === 201) {
            const mensagemSucesso = await response.text();
            alert(mensagemSucesso); // Exibe: "Checkout finalizado com sucesso! ID da Transação..."

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

// ==========================================
// RENDERIZAÇÃO E UI LOCAL
// ==========================================
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
                <button class="btn-remove" onclick="removeFromCart('${item.idPatente}')">Remover</button>
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
            ${currentUserRole !== 'NIT' ? `
                <button class="btn-add-cart" onclick="addToCart('${p.idPatente}')">
                    Manifestar Interesse
                </button>
            ` : ''}
        </div>
    `).join('');
}

// Inicializa a aplicação buscando dados do Java
fetchPatents();