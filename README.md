# TACS 2026 1C - Grupo 5

Aplicación de intercambio de figuritas del Mundial. Permite a los usuarios gestionar sus colecciones, proponer intercambios, participar en subastas y calificarse entre sí.

## Stack tecnológico

### Backend

| Componente  | Tecnología        |
|-------------|-------------------|
| Lenguaje    | Java 17           |
| Framework   | Spring Boot 3.2.5 |
| Build       | Maven 3.9+        |
| Tests       | JUnit 5 + Mockito |
| Cobertura   | JaCoCo            |
| Calidad     | SpotBugs          |
| Boilerplate | Lombok            |

### Frontend

| Componente  | Tecnología         |
|-------------|--------------------|
| Lenguaje    | JavaScript         |
| Framework   | React 19           |
| Build       | Vite 8             |
| Router      | React Router 7     |
| HTTP Client | Axios              |
| Servidor    | Nginx (producción) |

### Infraestructura

| Componente       | Tecnología                 |
|------------------|----------------------------|
| Containerización | Docker (multi-stage build) |
| Orquestación     | Docker Compose             |
| CI               | GitHub Actions             |

## Comandos

El proyecto usa un `Makefile` para unificar todos los comandos.

### Prod

| Comando           | Descripción                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `make build`      | Compila las imágenes y levanta el stack en background                       |
| `make up`         | Levanta el stack sin recompilar (requiere haber corrido `make build` antes) |
| `make down`       | Baja el stack                                                               |
| `make logs`       | Muestra los logs de todos los servicios en tiempo real                      |
| `make logs-back`  | Logs solo del backend                                                       |
| `make logs-front` | Logs solo del frontend                                                      |

| Servicio | URL                   |
|----------|-----------------------|
| Frontend | http://localhost:5173 |
| Backend  | http://localhost:8080 |

### Dev

| Comando          | Descripción                                          |
|------------------|------------------------------------------------------|
| `make dev`       | Levanta el stack completo con Compose Watch activo   |
| `make dev-down`  | Baja el stack de desarrollo                          |
| `make dev-back`  | Levanta o recarga solo el backend                    |
| `make dev-front` | Levanta o recarga solo el frontend                   |
| `make test`      | Corre los tests del backend en un contenedor efímero |

En modo desarrollo el frontend tiene hot reaload. Al guardar cualquier archivo en `src/`
Vite actualiza el browser sin recargar la página.
El backend se recarga manualmente con `make dev-back` cuando se cambia código Java.

### Variables de entorno

Crear un `.env` en la raíz a partir del ejemplo antes de levantar el stack:

```bash
cp .env.example .env
```

Los valores por defecto funcionan para dev sin modificaciones.
En prod reemplazar `MONGO_URI` con la URI de Atlas y `CORS_ORIGIN` con el dominio real.

---

## Endpoints disponibles

### Perfil

| Método | Endpoint                             | Descripción                                                                                |
|--------|--------------------------------------|--------------------------------------------------------------------------------------------|
| GET    | `/perfil/{user_id}/operaciones`      | Retorna figuritas publicadas, propuestas enviadas/recibidas y subastas activas del usuario |
| GET    | `/perfil/{user_id}/intercambiables`  | Lista las figuritas intercambiables del usuario                                            |
| GET    | `/perfil/{user_id}/sugerencias`      | Sugiere perfiles que tienen figuritas que le faltan al usuario                             |
| GET    | `/perfil/{user_id}/notificaciones`   | Lista las notificaciones del usuario                                                       |
| POST   | `/perfil/{perfil_id}/calificaciones` | Agrega una calificación (1–5) a un perfil                                                  |

**Header requerido en calificaciones:** `autor_id`

**Body de calificación:**

```json
{
  "valor": 4,
  "descripcion": "Buen intercambio"
}
```

### Propuestas

| Método | Endpoint                         | Descripción                       |
|--------|----------------------------------|-----------------------------------|
| POST   | `/propuestas`                    | Crea una propuesta de intercambio |
| PATCH  | `/propuestas/{prop_id}/aceptar`  | Acepta una propuesta pendiente    |
| PATCH  | `/propuestas/{prop_id}/rechazar` | Rechaza una propuesta pendiente   |

**Body de creación:**

```json
{
  "autor_id": "1000",
  "destinatario_id": "1001",
  "figurita_buscada_id": "ARG-10",
  "figuritas_ofrecidas_ids": [
    "FRA-10",
    "BRA-11"
  ]
}
```

### Subastas

| Método | Endpoint                        | Descripción                        |
|--------|---------------------------------|------------------------------------|
| POST   | `/subastas`                     | Crea una subasta para una figurita |
| POST   | `/subastas/{sub_id}/propuestas` | Oferta en una subasta existente    |

**Header requerido:** `user_id`

**Body de creación:**

```json
{
  "figurita_id": "ARG-10",
  "duracion": 60
}
```

**Body de oferta:**

```json
{
  "usuario_id": "1001",
  "figuritas_ofrecidas_id": [
    "FRA-10"
  ]
}
```

### Colecciones

| Método | Endpoint                          | Descripción                                              |
|--------|-----------------------------------|----------------------------------------------------------|
| POST   | `/colecciones/{col_id}/faltantes` | Agrega una figurita a la lista de faltantes              |
| POST   | `/colecciones/{col_id}/repetidas` | Agrega una figurita repetida disponible para intercambio |

**Header requerido en repetidas:** `user_id`

**Body de faltante:**

```json
{
  "fig_id": "ARG-10"
}
```

**Body de repetida:**

```json
{
  "fig_id": "ARG-10",
  "cantidad_disponible": 2,
  "modos_intercambio": [
    "INTERCAMBIO",
    "SUBASTA"
  ]
}
```

### Figuritas

| Método | Endpoint     | Descripción                                                         |
|--------|--------------|---------------------------------------------------------------------|
| GET    | `/figuritas` | Lista figuritas intercambiables con filtros opcionales y paginación |

El endpoint soporta dos modos de búsqueda mutuamente excluyentes según si se envía `q`:

**Modo búsqueda libre (`q` presente):** OR entre campos, AND entre términos.

| Param           | Tipo    | Requerido | Default | Descripción                                                                                                           |
|-----------------|---------|-----------|---------|-----------------------------------------------------------------------------------------------------------------------|
| `q`             | String  | Sí        | —       | Texto libre; términos separados por espacio se combinan con AND, cada uno busca en jugador, selección y número con OR |
| `tipo`          | Enum    | No        | —       | `INTERCAMBIO` o `SUBASTA`; ausente devuelve todos                                                                     |
| `pagina`        | Integer | No        | `0`     | Página solicitada (0-indexed)                                                                                         |
| `tamanioPagina` | Integer | No        | `12`    | Tamaño de página (máximo 40)                                                                                          |

**Modo filtros estructurados (`q` ausente):** AND entre todos los parámetros.

| Param           | Tipo    | Requerido | Default | Descripción                                            |
|-----------------|---------|-----------|---------|--------------------------------------------------------|
| `numero`        | Integer | No        | —       | Número exacto de figurita                              |
| `seleccion`     | Enum    | No        | —       | `ARGENTINA`, `BRASIL`, `FRANCIA`, `ESPAÑA`, `ALEMANIA` |
| `jugador`       | String  | No        | —       | Nombre del jugador (contains, case-insensitive)        |
| `tipo`          | Enum    | No        | —       | `INTERCAMBIO` o `SUBASTA`; ausente devuelve todos      |
| `pagina`        | Integer | No        | `0`     | Página solicitada (0-indexed)                          |
| `tamanioPagina` | Integer | No        | `12`    | Tamaño de página (máximo 40)                           |

**Respuesta:**

```json
{
  "contenido": [
    {
      "figurita_id": "ARG-10",
      "numero": 10,
      "jugador": "Messi",
      "posicion": "Delantero",
      "seleccion": "ARGENTINA",
      "cantidad_existente": 3,
      "cantidad_reservada": 0,
      "metodos": [
        "INTERCAMBIO"
      ],
      "usuario_id": "1000",
      "nombre_usuario": "Lucas",
      "reputacion": 4
    }
  ],
  "cantidad_de_elementos": 247,
  "cantidad_de_paginas": 21,
  "numero": 0
}
```

Cuando no hay resultados se retorna `200` con `content: []` y `total_elements: 0`.

### Administrador

| Método | Endpoint                      | Descripción                                                           |
|--------|-------------------------------|-----------------------------------------------------------------------|
| GET    | `/administrador/estadisticas` | Retorna estadísticas globales (usuarios, figuritas, subastas activas) |

---

## Colección de Postman

El archivo `postman_collection.json` en la raíz del repositorio contiene todos los endpoints preconfigurados con ejemplos de request. Para usarla:

1. Abrir Postman
2. **Import** > seleccionar `postman_collection.json`
3. Levantar la aplicación y ejecutar los requests

Los IDs de perfiles, figuritas y colecciones usados en los ejemplos corresponden a los datos de prueba precargados al iniciar la app.

---

## Decisiones de diseño

### Docker: multi-stage builds

Tanto el backend como el frontend usan [multi-stage builds](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/). La imagen final no contiene el compilador ni las dependencias de build, solo el artefacto ejecutable.

Cada Dockerfile define stages separados para desarrollo y producción:

```
# Backend
dependencies  →  mvn dependency:go-offline (deps cacheadas)
dev           →  + código fuente, sin tests
build         →  + mvn verify (tests + SpotBugs + JaCoCo)
runtime       →  JAR en JRE Alpine (imagen final de prod)

# Frontend
deps          →  npm ci (node_modules cacheados)
dev           →  + código fuente, sin build
builder       →  + npm run build
production    →  Nginx con /dist (imagen final de prod)
```

### Docker: entorno de desarrollo con Compose Watch

El stack de desarrollo usa `docker-compose.dev.yml`, que buildea hasta el stage `dev` de cada Dockerfile (sin `mvn verify` ni `npm run build`).

Los cambios en el código fuente se sincronizan al contenedor usando [Compose Watch](https://fsck.sh/en/blog/docker-compose-watch-modern-workflows/),
que transfiere archivos a través del socket Docker sin requerir volume mounts ni configuración especial del host.
Para el frontend, Vite detecta el cambio y actualiza el browser automáticamente (Hot Reload).
Para el backend, el redespliegue es manual con `make dev-back`.

### Docker: health check y orden de inicio

El backend expone `/ping` como endpoint de salud. Docker Compose espera que esté `healthy` antes de iniciar el frontend (`condition: service_healthy`),
evitando que las primeras llamadas a la API fallen durante el arranque ([referencia](https://docs.docker.com/compose/how-tos/startup-order/)).
El `start_period` de 15s le da margen a Spring Boot para inicializar.

Ambos servicios tienen `restart: unless-stopped` para que Docker los recupere automáticamente ante una caída.

### CORS configurable

El origin permitido se define en `application.properties` y puede sobreescribirse vía variable de entorno `CORS_ORIGIN`. Esto permite usar `http://localhost:5173` en desarrollo y la URL real en producción sin recompilar.

### `MetodoIntercambio` como enum y no como entidad

Los métodos de intercambio son un conjunto cerrado y conocido en tiempo de compilación (`INTERCAMBIO`, `SUBASTA`). Modelarlos como enum evita una tabla extra en la base de datos, elimina joins innecesarios y permite usar el compilador como validación. Si en el futuro aparece un nuevo método de
intercambio, el cambio es deliberado y controlado.

### La colección tiene su propio repositorio separado del perfil

`Coleccion` es un agregado con identidad propia y lógica no trivial (deduplicación de faltantes, acumulación de repetidas). Separar su repositorio del de `Perfil` respeta el principio de responsabilidad única y facilita la futura migración a base de datos, donde serán tablas distintas. Además,
permite que operaciones sobre la colección no requieran cargar el perfil completo.

---
## Utilización de la IA
---

## Ejecutar tests

```bash
make test
```

Corre los tests dentro de un contenedor efímero sin levantar el stack completo. No requiere Java instalado localmente.

## Validar el proyecto

```bash
docker compose -f docker-compose.dev.yml run --rm backend mvn verify
```

Ejecuta tests, análisis de SpotBugs y cobertura mínima con JaCoCo (80%). Este mismo comando corre automáticamente en cada `make build` (producción).

## Configuración del IDE (IntelliJ)

### SDK de Java 17

En **File > Project Structure > Project**, seleccionar SDK 17 y language level 17.

### Fin de línea Unix

En **File > Settings > Editor > Code Style**, seleccionar `Unix and OS X (\n)` en **Line separator**.

### Indentación con 2 espacios

En **File > Settings > Editor > Code Style > Java > Tabs and Indents**, setear Tab size, Indent y Continuation indent en 2, 2 y 4.
