// loadtest/scripts/figuritas.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { login } from './helpers/auth.js';

export const options = {
    stages: [
        { duration: '10s', target: 10  },  // calentamiento
        { duration: '15s',  target: 10  },  // carga normal
        { duration: '20s', target: 50  },  // pico de tráfico
        { duration: '30s',  target: 50  },  // sotiene pico
        { duration: '10s', target: 0   },  // bajada
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed:   ['rate<0.01'],
    },
};

const BASE = 'http://backend-test:8080';
const USUARIO = { nombre: 'lucas_fis', contrasenia: 'Gordo123!' };

export default function () {
    const cookie = login(BASE, USUARIO);
    const authHeaders = { 'Cookie': `token=${cookie}` };

    const res = http.get(`${BASE}/perfil/sugerencias`, { headers: authHeaders });
    check(res, { 'sugerencias: status 200': r => r.status === 200 });

    sleep(1);
}