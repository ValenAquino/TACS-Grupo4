// loadtest/scripts/figuritas.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { login } from './helpers/auth.js';
import {optionsDefault} from './helpers/options.js'

export const options = optionsDefault

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