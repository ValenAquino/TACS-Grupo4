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
