/* let patents = [
    { id: 1, title: "Processo de obtenção de biomassa de fruta-pão", area: "Alimentos", inventor: "M.B.S. Feijó; I.L.G.T. Almeida", desc: "Produção de biomassa verde e madura para aplicações industriais." },
    { id: 2, title: "Extrato contra o vírus HIV-1 a partir de produtos marinhos", area: "Saúde", inventor: "I.C.N.P. Paixão; V.L. Teixeira", desc: "Ação inibitória da replicação viral utilizando algas marinhas." },
    { id: 3, title: "Ritmáximo: Software de educação musical", area: "Tecnologia", inventor: "F.A.P. Guilhon; A.C.S. Figueiredo", desc: "Software para desenvolvimento rítmico para deficientes visuais." },
    { id: 4, title: "Iluminador Subcutâneo para Terapia Intravenosa", area: "Saúde", inventor: "E.C. Santana; N.M.A. Figueiredo", desc: "Dispositivo para facilitar a visualização da rede venosa." },
    { id: 5, title: "Nursing Alert: Aplicativo de suporte clínico", area: "Tecnologia", inventor: "L.S. Andrade; R.F.A. Silva", desc: "Suporte à decisão clínica para diagnósticos de enfermagem." }
];
let cart = [];
let currentUserRole = "VISITANTE"; */

const API_BASE_URL = "http://localhost:8080/api"; // Ajustar conforme a porta do Java!!!
let patents = []; // Começa vazio, será preenchido pelo servidor

function showSection(id) {
    document.querySelectorAll('.page-section').forEach(s => s.classList.remove('active-section'));
    document.getElementById(id).classList.add('active-section');
    if(id === 'home') renderPatents(patents);
}

function showRegisterForm(role) {
    currentUserRole = role;
    document.getElementById('reg-title').innerText = "Cadastro de " + role;
    showSection('register-form');
}

function handleRegistration(event) {
    event.preventDefault();
    alert("Cadastrado como " + currentUserRole);
    currentUserRole === 'NIT' ? loginAsNIT() : loginAsUser(currentUserRole);
}

function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('login-email').value;
    email.includes('nit') ? loginAsNIT() : loginAsUser("PESQUISADOR");
}

function loginAsNIT() {
    currentUserRole = "NIT";
    
    // Ocultar menus de visitante/comprador
    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-cart').classList.add('hidden');
    
    // Mostrar menus do NIT
    document.getElementById('nav-nit').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');
    
    showSection('nit-panel');
    renderPatents(patents); // Recarrega para remover botões da vitrine
}

function loginAsUser(role) {
    currentUserRole = role;
    
    // Ocultar menus de visitante/NIT
    document.getElementById('nav-reg').classList.add('hidden');
    document.getElementById('nav-login').classList.add('hidden');
    document.getElementById('nav-nit').classList.add('hidden');
    
    // Mostrar menus de usuário comum
    document.getElementById('nav-cart').classList.remove('hidden');
    document.getElementById('nav-logout').classList.remove('hidden');
    
    showSection('home');
    renderPatents(patents); // Recarrega para mostrar botões
}

function logout() { location.reload(); }

async function handlePatentSubmit(event) {
    event.preventDefault();
    const newP = {
        title: document.getElementById('p-title').value,
        area: document.getElementById('p-area').value,
        inventor: document.getElementById('p-inv').value,
        desc: document.getElementById('p-desc').value
    };

    const response = await fetch(`${API_BASE_URL}/patentes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newP)
    });

    if (response.ok) {
        alert("Publicado no Java com sucesso!");
        fetchPatents(); // Recarrega a lista do servidor
        showSection('home');
    }
}

async function fetchPatents() {
    try {
        const response = await fetch(`${API_BASE_URL}/patentes`);
        patents = await response.json(); // O Java retorna o JSON das patentes
        renderPatents(patents);
    } catch (error) {
        console.error("Erro ao buscar patentes:", error);
    }
}

    function addToCart(id) {
        // 1. Verificações de permissão
        if (currentUserRole === "VISITANTE") {
            alert("Atenção: Você precisa estar logado para adicionar patentes ao carrinho.");
            showSection('login-section');
            return;
        }

        if (currentUserRole === "NIT") {
            alert("Gestores do NIT não realizam aquisições.");
            return;
        }

        // 2. Verifica se a patente já está no carrinho
        const itemExistente = cart.find(item => item.id === id);
        if (itemExistente) {
            alert("Esta patente já foi adicionada ao seu carrinho.");
            return;
        }

        // 3. Adiciona a patente ao array
        const patenteParaAdicionar = patents.find(p => p.id === id);
        cart.push(patenteParaAdicionar);

        // 4. Atualiza a interface
        updateCartUI();
        
        // 5. ABRE O MODAL AUTOMATICAMENTE
        toggleCart(); 
    }

    function toggleCart() { 
        document.getElementById('cart-modal').classList.toggle('active'); 
    }

    function removeFromCart(id) {
        // Remove o item do array
        cart = cart.filter(item => item.id !== id);
        
        // Atualiza a interface
        updateCartUI();
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
                    <span>${item.title}</span>
                    <!-- O ID precisa estar aqui -->
                    <button class="btn-remove" onclick="removeFromCart(${item.id})">Remover</button>
                </div>
            `).join('');
        }
    }

    function finalizarInteresse() {
        // Verifica se o carrinho está vazio
        if (cart.length === 0) {
            alert("Seu carrinho está vazio! Adicione patentes antes de finalizar.");
            return; // Interrompe a função
        }

        // Se tiver itens, segue com o processo
        alert("Manifestação de interesse enviada com sucesso! O NIT entrará em contato.");
        
        // Opcional: Limpa o carrinho após finalizar
        cart = [];
        updateCartUI();
        toggleCart();
    }

fetchPatents();