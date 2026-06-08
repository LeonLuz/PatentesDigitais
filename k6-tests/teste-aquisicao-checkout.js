import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

export let CheckoutDuration = new Trend('checkout_duration');
export let CheckoutFailRate = new Rate('checkout_fail_rate');
export let CheckoutConflictRate = new Rate('checkout_conflict_rate');
export let CheckoutSuccessRate = new Rate('checkout_success_rate');
export let CheckoutReqs = new Counter('checkout_reqs');

export const options = {
  stages: [
    { duration: '15s', target: 10 },
    { duration: '30s', target: 10 },

    { duration: '15s', target: 20 },
    { duration: '30s', target: 20 },

    { duration: '15s', target: 40 },
    { duration: '30s', target: 40 },

    { duration: '15s', target: 60 },
    { duration: '30s', target: 60 },

    { duration: '15s', target: 80 },
    { duration: '30s', target: 80 },

    { duration: '15s', target: 100 },
    { duration: '45s', target: 100 },

    { duration: '20s', target: 0 },
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

    CheckoutReqs.add(1);
    CheckoutDuration.add(resCheckout.timings.duration);

    const isSuccess = resCheckout.status === 201;
    const isConflict = resCheckout.status === 409;
    const isRealFailure = !isSuccess && !isConflict;

    CheckoutSuccessRate.add(isSuccess);
    CheckoutConflictRate.add(isConflict);
    CheckoutFailRate.add(isRealFailure);

    check(resCheckout, {
      '2. Emissão de Checkout Processada (201 ou 409)': (r) => r.status === 201 || r.status === 409,
    });

    if (isSuccess && resCheckout.body) {
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