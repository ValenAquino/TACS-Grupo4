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

    const res = http.get(`${BASE}/propuestas`, { headers: authHeaders });

    const intercambios = res.json(); // parseo único

    check(res, {
        'status 200':      (r) => r.status === 200,
        'body no vacio':   (r) => r.body.length > 0,
    });

    check(intercambios, {
        'esta paginado':      (i) => i.numero != null && typeof i.numero === 'number',
        'contenido es array': (i) => Array.isArray(i.contenido),
        'tiene elementos':    (i) => i.contenido.length > 0,
        'tienen id': (i) => i.contenido.every(inter => inter.id !== null && inter.id !== ""),
        'tienen autor y destinatario': (i) => i.contenido.every(inter => inter.autor != null && inter.destinatario != null),
        'tienen figurita buscada': (i) => i.contenido.every(inter => inter.figurita_buscada != null),
        'tienen figuritas ofrecidas': (i) => i.contenido.every(inter => inter.figuritas_ofrecidas.length > 0)
    });

    sleep(1);
}