import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 20 },
    { duration: '30s', target: 20 },
    { duration: '10s', target: 0 }, 
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'], 
  },
};

// Ajustado para gerar 11 números (tamanho real de um CPF)
function gerarCpfValido() {
    let result = '';
    for (let i = 0; i < 11; i++) {
        result += Math.floor(Math.random() * 10);
    }
    return result;
}

export default function () {
  const url = 'http://localhost:8080/api/usuarios/pesquisador'; 
  
  const idAleatorio = Math.floor(Math.random() * 1000000);

  // Payload ajustado para bater perfeitamente com a entidade Pesquisador
  const payload = JSON.stringify({
    nome: `Pesquisador Teste ${idAleatorio}`,
    cpf: gerarCpfValido(), // <--- O NOME E O TAMANHO CORRETOS!
    email: `pesquisador${idAleatorio}@gmail.com`,
    senha: 'senha-super-segura'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    'status é 201 (Created)': (r) => r.status === 201,
  });

  sleep(1);
}