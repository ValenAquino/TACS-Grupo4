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

    const res = http.get(`${BASE}/subastas`, { headers: authHeaders });

    const subastas = res.json(); // parseo único

    checkHttp(res, 200)

    checkPaginacion(subastas);

    check(subastas.contenido, {
        'tienen id': (s) => s.every(sub => sub.id !== null && sub.id !== ""),
        'tienen figurita subastada': (s) => s.every(sub => sub.figurita_subastada != null),
        'tienen cierre': (s) => s.every(sub => sub.fecha_inicio != null && sub.fecha_cierre != null)
    });

    sleep(1);
}