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

    const res = http.get(`${BASE}/colecciones/faltantes`, { headers: authHeaders });

    const faltantes = res.json();

    checkHttp(res, 200)

    checkPaginacion(faltantes);

    check(faltantes.contenido, {
        'con id definido': (f) => f.every(fig => fig.id !== null && fig.id !== ""),
        'con numero':      (f) => f.every(fig => typeof fig.numero === 'number'),
        'con jugador':     (f) => f.every(fig => typeof fig.jugador === 'string'),
    });

    sleep(1);
}