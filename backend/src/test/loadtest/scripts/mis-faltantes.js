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

    const res = http.get(`${BASE}/colecciones/faltantes`, { headers: authHeaders });

    const faltantes = res.json();

    check(res, {
        'status 200':      (r) => r.status === 200,
        'body no vacio':   (r) => r.body.length > 0,
    });

    check(faltantes, {
        'esta paginado':      (s) => s.numero != null && typeof s.numero === 'number',
        'contenido es array': (s) => Array.isArray(s.contenido),
        'no esta vacio':   (f) => f.length > 0,
        'con id definido': (f) => f.contenido.every(fig => fig.id !== null && fig.id !== ""),
        'con numero':      (f) => f.contenido.every(fig => typeof fig.numero === 'number'),
        'con jugador':     (f) => f.contenido.every(fig => typeof fig.jugador === 'string'),
    });

    sleep(1);
}