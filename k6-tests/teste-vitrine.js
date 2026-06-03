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

export default function () {
  const url = 'http://localhost:8080/api/patentes/vitrine';

  const res = http.get(url);

  check(res, {
    'status é 200 (Sucesso)': (r) => r.status === 200,
  });

  sleep(1);
}