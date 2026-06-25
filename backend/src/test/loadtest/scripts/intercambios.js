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

    const res = http.get(`${BASE}/propuestas`, { headers: authHeaders });

    const intercambios = res.json(); // parseo único

    checkHttp(res, 200)

    checkPaginacion(intercambios);

    check(intercambios, {
        'tienen id': (i) => i.contenido.every(inter => inter.id !== null && inter.id !== ""),
        'tienen autor y destinatario': (i) => i.contenido.every(inter => inter.autor != null && inter.destinatario != null),
        'tienen figurita buscada': (i) => i.contenido.every(inter => inter.figurita_buscada != null),
        'tienen figuritas ofrecidas': (i) => i.contenido.every(inter => inter.figuritas_ofrecidas.length > 0)
    });

    sleep(1);
}