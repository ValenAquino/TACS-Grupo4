import http from 'k6/http';
import { check, sleep } from 'k6';
import { login } from './helpers/auth.js';
import {optionsDefault} from "./helpers/options.js";
import {checkHttp, checkPaginacion} from "./helpers/checks.js";

export const options = optionsDefault

const BASE = 'http://backend-test:8080';
const USUARIO = { nombre: 'lucas_fis', contrasenia: 'Gordo123!' };

export default function () {
    const cookie = login(BASE, USUARIO);
    const authHeaders = { 'Cookie': `token=${cookie}` };

    const res = http.get(`${BASE}/perfil/sugerencias`, { headers: authHeaders });

    const sugerencias = res.json(); // parseo único

    checkHttp(res, 200)

    checkPaginacion(sugerencias);

    check(sugerencias.contenido, {
        'tiene recomendadas': (s) => s.every(c => Array.isArray(c.figuritas_recomendadas)),
        'tiene necesarias':   (s) => s.every(c => Array.isArray(c.figuritas_necesarias)),
        'tiene perfil':       (s) => s.every(c => c.perfil != null),
    });

    sleep(1);
}