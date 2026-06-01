import http from 'k6/http';
import { check, sleep, fail } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

export let CheckoutDuration = new Trend('checkout_duration');
export let CheckoutFailRate = new Rate('checkout_fail_rate');
export let CheckoutSuccessRate = new Rate('checkout_success_rate');
export let CheckoutReqs = new Counter('checkout_reqs');

export const options = {
  stages: [
    { duration: '15s', target: 5 },
    { duration: '30s', target: 5 },

    { duration: '15s', target: 10 },
    { duration: '30s', target: 10 },

    { duration: '15s', target: 15 },
    { duration: '30s', target: 15 },

    { duration: '15s', target: 20 },
    { duration: '30s', target: 20 },

    { duration: '15s', target: 0 },
  ],
  thresholds: {
    'checkout_duration': ['p(95)<500'],
    'checkout_fail_rate': ['rate<0.01'],
  },
};

export function setup() {
  const urlUsuarios = 'http://localhost:8080/api/usuarios/ids';
  const urlPatentes = 'http://localhost:8080/api/patentes/ids';

  const resUsuarios = http.get(urlUsuarios);
  const resPatentes = http.get(urlPatentes);

  if (resUsuarios.status !== 200 || resPatentes.status !== 200) {
    throw new Error(`⚠️ Erro no Setup: Status Usuários: ${resUsuarios.status} | Status Patentes: ${resPatentes.status}`);
  }

  const usuarios = resUsuarios.json();
  const patentes = resPatentes.json();

  if (usuarios.length === 0 || patentes.length === 0) {
    throw new Error("⚠️ Erro no Setup: O banco local de usuários ou patentes está vazio!");
  }

  return {
    usuariosDoBanco: usuarios,
    patentesDoBanco: patentes
  };
}

export default function (data) {
  const headersJson = { headers: { 'Content-Type': 'application/json' } };

  const usuariosOrgsDoBanco = data.usuariosDoBanco;
  const todasPatentesDoBanco = data.patentesDoBanco;

  // Forçar a VU a usar sempre o mesmo usuário para evitar race condition do mesmo usuário
  const indexUsuario = (__VU - 1) % usuariosOrgsDoBanco.length;
  const idUsuario = usuariosOrgsDoBanco[indexUsuario];
  const idPatente = todasPatentesDoBanco[Math.floor(Math.random() * todasPatentesDoBanco.length)];

  const urlCarrinho = `http://localhost:8080/api/carrinho/${idUsuario}/itens`;
  const payloadCarrinho = JSON.stringify({ idPatente: idPatente });
  const resCarrinho = http.post(urlCarrinho, payloadCarrinho, headersJson);

  const carrinhoPronto = check(resCarrinho, {
    '1. Item adicionado ao carrinho (201)': (r) => r.status === 201 || r.status === 400,
  });

  if (carrinhoPronto && resCarrinho.status === 201) {
    const urlCheckout = `http://localhost:8080/api/aquisicoes/checkout/${idUsuario}`;
    const resCheckout = http.post(urlCheckout, null);

    // Alimentando as métricas
    CheckoutReqs.add(1);
    CheckoutDuration.add(resCheckout.timings.duration);
    CheckoutFailRate.add(resCheckout.status !== 201);
    CheckoutSuccessRate.add(resCheckout.status === 201);

    const checkoutSucesso = check(resCheckout, {
      '2. Checkout de Aquisição Realizado (201)': (r) => r.status === 201,
    });

    if (!checkoutSucesso) {
      fail(`Checkout falhou criticamente com status: ${resCheckout.status}`);
    }

    // Cancela a aquisição para liberar as patentes para os próximos usuários
    if (resCheckout.body) {
      const match = resCheckout.body.match(/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/i);
      if (match) {
        const idAquisicaoCriada = match[0];
        const urlCancelar = `http://localhost:8080/api/aquisicoes/cancelar/${idAquisicaoCriada}`;
        http.put(urlCancelar);
      }
    }
  }

  sleep(1);
}