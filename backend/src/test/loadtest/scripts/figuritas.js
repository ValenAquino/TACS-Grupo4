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
        'http_req_failed': ['rate<0.01'],
        'checks': ['rate>0.99'],
    },
};

const BASE = 'http://backend-test:8080';
const USUARIO = { nombre: 'lucas_fis', contrasenia: 'Gordo123!' };

export default function () {
    const cookie = login(BASE, USUARIO);
    const authHeaders = { 'Cookie': `token=${cookie}` };

    const res = http.get(`${BASE}/figuritas`, { headers: authHeaders });

    const figuritas = res.json(); // parseo único

    check(res, {
        'status 200':      (r) => r.status === 200,
        'body no vacio':   (r) => r.body.length > 0,
    });

    check(figuritas, {
        'es un array':     (f) => Array.isArray(f),
        'no esta vacio':   (f) => f.length > 0,
        'con id definido': (f) => f.every(fig => fig.id !== null && fig.id !== ""),
        'con numero':      (f) => f.every(fig => typeof fig.numero === 'number'),
        'con jugador':     (f) => f.every(fig => typeof fig.jugador === 'string'),
    });

    sleep(1);
}