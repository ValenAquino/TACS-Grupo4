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

    const res = http.get(`${BASE}/perfil/sugerencias`, { headers: authHeaders });

    const sugerencias = res.json(); // parseo único

    check(res, {
        'status 200':      (r) => r.status === 200,
        'body no vacio':   (r) => r.body.length > 0,
    });

    check(sugerencias, {
        'esta paginado':      (s) => s.numero != null && typeof s.numero === 'number',
        'contenido es array': (s) => Array.isArray(s.contenido),
        'tiene elementos':    (s) => s.contenido.length > 0,
        'tiene recomendadas': (s) => s.contenido.every(c => Array.isArray(c.figuritas_recomendadas)),
        'tiene necesarias':   (s) => s.contenido.every(c => Array.isArray(c.figuritas_necesarias)),
        'tiene perfil':       (s) => s.contenido.every(c => c.perfil != null),
    });

    sleep(1);
}