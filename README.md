# TACS 2026 1C - Grupo 5

Aplicación de intercambio de figuritas del Mundial. Permite a los usuarios gestionar sus colecciones, proponer intercambios, participar en subastas y calificarse entre sí.

## Stack tecnológico

| Componente       | Tecnología                 |
|------------------|----------------------------|
| Lenguaje         | Java 17                    |
| Framework        | Spring Boot 3.2.5          |
| Build            | Maven 3.9+                 |
| Tests            | JUnit 5 + Mockito          |
| Cobertura        | JaCoCo                     |
| Calidad          | SpotBugs                   |
| Boilerplate      | Lombok                     |
| Containerización | Docker (multi-stage build) |
| CI               | GitHub Actions             |

## Levantar la aplicación

### Con Docker (recomendado)

```bash
docker build -t tacs-grupo5 .
docker run -p 8080:8080 tacs-grupo5
```

### Con Maven

```bash
mvn spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

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

| Método | Endpoint     | Descripción                                                       |
|--------|--------------|-------------------------------------------------------------------|
| GET    | `/figuritas` | Lista figuritas intercambiables con filtros opcionales y paginación |

**Query params:**

| Param           | Tipo    | Requerido | Default | Descripción                                          |
|-----------------|---------|-----------|---------|------------------------------------------------------|
| `numero`        | Integer | No        | —       | Número exacto de figurita                            |
| `seleccion`     | Enum    | No        | —       | `ARGENTINA`, `BRASIL`, `FRANCIA`, `ESPAÑA`, `ALEMANIA` |
| `jugador`       | String  | No        | —       | Nombre del jugador (contains, case-insensitive)      |
| `tipo`          | Enum    | No        | —       | `INTERCAMBIO` o `SUBASTA`; ausente devuelve todos   |
| `pagina`        | Integer | No        | `0`     | Página solicitada (0-indexed)                        |
| `tamanioPagina` | Integer | No        | `12`    | Tamaño de página (máximo 40)                         |
| `ordenar`       | String  | No        | `numero` | `numero` (asc) o `reputacion` (desc)                |

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
      "metodos": ["INTERCAMBIO"],
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

### `MetodoIntercambio` como enum y no como entidad

Los métodos de intercambio son un conjunto cerrado y conocido en tiempo de compilación (`INTERCAMBIO`, `SUBASTA`). Modelarlos como enum evita una tabla extra en la base de datos, elimina joins innecesarios y permite usar el compilador como validación. Si en el futuro aparece un nuevo método de
intercambio, el cambio es deliberado y controlado.

### La colección tiene su propio repositorio separado del perfil

`Coleccion` es un agregado con identidad propia y lógica no trivial (deduplicación de faltantes, acumulación de repetidas). Separar su repositorio del de `Perfil` respeta el principio de responsabilidad única y facilita la futura migración a base de datos, donde serán tablas distintas. Además,
permite que operaciones sobre la colección no requieran cargar el perfil completo.

---

## Ejecutar tests

```bash
mvn test
```

## Validar el proyecto

```bash
mvn clean verify
```

Este comando ejecuta los tests, corre el análisis de SpotBugs y valida la cobertura mínima con JaCoCo.

## Configuración del IDE (IntelliJ)

### SDK de Java 17

En **File > Project Structure > Project**, seleccionar SDK 17 y language level 17.

### Fin de línea Unix

En **File > Settings > Editor > Code Style**, seleccionar `Unix and OS X (\n)` en **Line separator**.

### Indentación con 2 espacios

En **File > Settings > Editor > Code Style > Java > Tabs and Indents**, setear Tab size, Indent y Continuation indent en 2, 2 y 4.

