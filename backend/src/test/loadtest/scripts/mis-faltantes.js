// loadtest/scripts/figuritas.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { login } from './helpers/auth.js';
import {optionsDefault} from "./helpers/options";

export const options = optionsDefault

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
        'no esta vacio':   (f) => f.contenido.length > 0,
        'con id definido': (f) => f.contenido.every(fig => fig.id !== null && fig.id !== ""),
        'con numero':      (f) => f.contenido.every(fig => typeof fig.numero === 'number'),
        'con jugador':     (f) => f.contenido.every(fig => typeof fig.jugador === 'string'),
    });

    sleep(1);
}