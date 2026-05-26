# Comparación: Persistencia mergeada (PR #104) vs. `feature/poc-mongodb`

> **Referencia A — Persistencia mergeada:** `efa4da1..8b5850d` (merge commit `8b5850d`, PR #104 `LucasFis/persistencia`)
> **Referencia B — PoC local:** rama `feature/poc-mongodb`, tip `4190281`
> **Merge base común:** `efa4da1` (ambos parten del mismo punto)

Este documento compara las dos implementaciones de persistencia MongoDB que existen sobre la misma base. La PoC es más chica y enfocada; la mergeada es más amplia y mezcla varios objetivos. Las decisiones arquitectónicas son **notablemente distintas** en varios puntos clave.

Para detalle de cada implementación por separado, ver `analisis-pr-104-persistencia.md` (la mergeada).

---

## 1. Tabla resumen ejecutiva

| Dimensión | PR #104 (mergeada) | `feature/poc-mongodb` (PoC) |
|---|---|---|
| **Tamaño** | 118 archivos, +3.364/-2.962 | 53 archivos, +1.275/-227 |
| **Scope declarado** | Persistencia + auth (Usuario, Calificacion, BCrypt) + rename masivo + interfaces eliminadas | Persistencia solamente |
| **Estrategia de migración** | Reemplaza `EnMemoria` por `Mongo` | Mantiene ambas, switch por Spring Profile |
| **Estructura de paquetes** | `repositories/impl/*Mongo.java` (un solo lugar) | `repositories/memoria/` + `repositories/mongodb/` |
| **Anotación de referencia** | `@DBRef` | `@DocumentReference` |
| **Anotación de constructor** | Lombok `@AllArgsConstructor` + `@NoArgsConstructor` | `@AllArgsConstructor(onConstructor_ = @PersistenceCreator)` |
| **Spring Boot** | 3.2.5 | 3.4.5 |
| **MongoDB target** | Atlas (URI con credenciales en el repo) | MongoDB local en Docker (URI por env var) |
| **Docker / Compose** | No incluido | Multi-stage Dockerfile + `docker-compose.yml` prod + `docker-compose.dev.yml` con watch + Makefile |
| **CORS** | Hardcoded `http://localhost:5173` | Por `@Value("${cors.allowed-origin}")` |
| **Calificacion como documento** | `@Document(collection = "calificaciones")` + `@DBRef` en Perfil | Embebida en `Perfil` (no `@Document`) |
| **FiguritaIntercambiable** | Embebida en `Coleccion` | Documento propio + `@DocumentReference` desde Coleccion |
| **`calificacionMedia` y `cantidadCalificaciones`** | Campos persistidos en `Perfil`; mutados por `agregarNuevaCalificacion` | Método `obtenerCalificacionMedia()` calculado on-demand |
| **Estadísticas** | Carga `buscarTodos()` en memoria, agrupa en Java | Usa `contarActivas()` y `contarPorEstado()` (aggregation pipeline) |
| **Nuevas entidades** | `Usuario`, `Calificacion` (como `@Document`) | Ninguna; `Usuario` mínima sin password |
| **Cobertura tests sobre Mongo** | Sí: flapdoodle embedded + `MongoTestBase` (lento) | **No**: solo tests sobre `EnMemoria`. Servicios con Mockito |
| **Rename de controladores/servicios** | Sí (`PerfilService` → `ServicioPerfil`, etc.) | No |
| **Eliminación de interfaces de servicio** | Sí | No |
| **`InicializadorDeDatos`** | `@Profile("!test")` | Sin guard; env var `APP_SEED_DATA` definida en compose pero no consumida |

---

## 2. Análisis dimensión por dimensión

### 2.1 Alcance del PR

**Mergeada (PR #104).** Mezcla cinco objetivos en un solo merge:
1. Migración a MongoDB.
2. Introducción de `Usuario` y `Calificacion` como entidades persistentes (preparación para auth).
3. Rename `*Service → Servicio*`, `*Controller → Controlador*`.
4. Eliminación de interfaces de servicio (`IPerfilService`, etc.).
5. Reorganización de filtros (`model.entities.filtros` → `dto.filtros`).

Esto explica el delta enorme y dificulta el review.

**PoC.** Cumple un solo objetivo: persistencia. No introduce entidades nuevas, no renombra, no toca interfaces de servicio. Como consecuencia el diff es **3x más chico** y muchísimo más reviewable.

> **Veredicto:** PoC gana en disciplina de scope. El PR #104 debería haberse partido en al menos 3 PRs separados.

### 2.2 Estrategia de migración: reemplazo vs. coexistencia

**Mergeada.** Borra las implementaciones `EnMemoria` y las reemplaza por las Mongo. No hay fallback.

**PoC.** Crea dos paquetes paralelos:

```
backend/src/main/java/app/repositories/
├── RepositorioColecciones.java          # interfaces
├── memoria/
│   └── RepositorioColeccionesEnMemoria.java   # @Profile("memoria")
└── mongodb/
    └── RepositorioColeccionesMongoDB.java     # @Profile("mongodb")
```

Spring elige uno u otro por `spring.profiles.active`:

```properties
spring.profiles.active=mongodb   # o "memoria"
```

**Trade-offs:**

| | Mergeada | PoC |
|---|---|---|
| Simplicidad de codebase | + (una sola impl) | − (dos impls a mantener) |
| Posibilidad de tests rápidos sin Mongo | depende de flapdoodle | + (basta cambiar profile) |
| Riesgo de divergencia entre impls | n/a | − (hay que mantener paridad) |
| Reversibilidad | − (rollback = revert) | + (cambiar profile) |
| Desarrollo offline | depende de embedded | + (memoria standalone) |
| Demo / CI rápida | − | + |

> **Veredicto:** Para un proyecto académico con entregas escalonadas y demo cliente, la estrategia de la PoC es más prudente. Mantenerla cuesta poco si las interfaces están bien definidas (que lo están).

### 2.3 `@DBRef` vs. `@DocumentReference`

**Mergeada (`@DBRef`).** Convención vieja de MongoDB que guarda referencias como sub-documento `{"$ref":"<col>", "$id":<id>}`.

```java
@DBRef private Perfil autor;
// Mongo guarda: { autor: { $ref: "perfiles", $id: "1000" } }
```

Las queries deben usar `autor.$id`:
```java
Criteria.where("autor.$id").is(perfilId)
```

**El PR #104 esto lo hace mal** (ver §3.2 del análisis correspondiente):
```java
Criteria.where("autor").is(perfilId)   // ← no matchea nunca
```

**PoC (`@DocumentReference`).** Anotación más moderna que guarda **solo el `_id`** del referenciado:

```java
@DocumentReference private Perfil autor;
// Mongo guarda: { autor: "1000" }
```

Las queries son directas:
```java
Criteria.where("autor").is(perfilId)   // ← funciona como se espera
```

Además, `@DocumentReference(lazy = true)` es trivial (en `Calificacion.autor`). Con `@DBRef` la carga eager está en el default y lazy requiere proxies.

> **Veredicto:** La PoC eligió la anotación correcta. Como bonus, evitó el bug sistémico de queries que afecta a la mergeada (`RepositorioPropuestasMongo.buscarPorAutorId`, `RepositorioNotificacionesMongo.buscarPorPerfil`, etc.).

### 2.4 `@PersistenceCreator` explícito

PoC anota los constructores `@AllArgsConstructor(onConstructor_ = @PersistenceCreator)`:

```java
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@Getter
public class Calificacion {
  private String id;
  @JsonIgnore
  @DocumentReference(lazy = true)
  private Perfil autor;
  ...
}
```

Esto le dice a Spring Data: "usá este constructor para deserializar desde Mongo" y elimina ambigüedades cuando hay varios constructores. La mergeada usa solo `@NoArgsConstructor + setters`, lo cual requiere que todos los campos sean mutables (limita inmutabilidad).

> **Veredicto:** PoC. Mejor decisión para entidades con datos casi inmutables (`Calificacion`, `Figurita`, `EstadoPropuesta`).

### 2.5 Embedded vs. document referenciado

**Calificacion**

- Mergeada: `@Document(collection = "calificaciones")` + `@DBRef List<Calificacion> calificaciones` en Perfil → colección separada `calificaciones`, referenciada con DBRef desde Perfil.
- PoC: Sin `@Document`, embebida directamente en `Perfil.calificaciones`.

**Trade-offs:**
- Embedded (PoC) — gana en lectura (un Perfil = un read), pero un Perfil con 10k calificaciones se vuelve pesado (Mongo límite 16MB/doc).
- Referenciada (Mergeada) — más escalable, pero requiere joins para construir el Perfil completo. **Mal aprovechado**: en `ServicioPerfil` no hay batch fetch, así que paga el costo de N+1 sin sacar el beneficio.

**FiguritaIntercambiable**

- Mergeada: embebida en `Coleccion.repetidas`.
- PoC: `@Document(collection = "figuritas_intercambiables")` + `@DocumentReference List<FiguritaIntercambiable> repetidas`.

> **Veredicto:** No hay una elección universalmente correcta. La PoC es más coherente — mantiene como `@Document` lo que necesita búsqueda autónoma (`buscarPorUsuarioId`, `buscarPorFiguritaIds`) y como embedded lo que es siempre accedido junto al padre. La mergeada hizo lo contrario (Calificacion como Document, FiguritaIntercambiable embebida) y produjo problemas en ambos lados (N+1 al cargar calificaciones, queries complejas con `$unwind` para buscar repetidas).

### 2.6 Cálculo de calificación media

**Mergeada.** Persiste `calificacionMedia: Double` y `cantidadCalificaciones: int`. Cada llamada a `agregarNuevaCalificacion` actualiza ambos campos vía media incremental (Welford):

```java
this.cantidadCalificaciones++;
this.calificacionMedia += (valor - this.calificacionMedia) / this.cantidadCalificaciones;
```

Riesgo: si alguien agrega a `calificaciones` directamente (`getCalificaciones().add(...)`, como hace `InicializadorDeDatos.cargarCalificaciones`), el estado denormalizado se desincroniza silenciosamente.

**PoC.** No persiste media. Calcula on demand:

```java
public double obtenerCalificacionMedia() {
  return calificaciones.stream()
      .mapToInt(Calificacion::getValor)
      .average()
      .orElse(1.0);   // ← default raro: "1.0" en vez de "0.0"
}
```

Más simple, sin riesgo de desincronización. Pero recorre toda la lista cada vez. Para el orden de magnitud del proyecto (decenas/cientos de calificaciones por usuario) es despreciable.

**Bug menor de la PoC**: `orElse(1.0)`. Un perfil sin calificaciones debería tener media 0 o `null`, no 1. Si esto se usa para filtrar subastas con calificación mínima, un usuario nuevo pasa por defecto. Cambiar a `orElse(0.0)`.

> **Veredicto:** PoC más simple y robusto en términos de consistencia. Fix del `1.0 → 0.0`.

### 2.7 Estadísticas

**Mergeada (`ServicioEstadisticas.obtenerEstadisticas`):**

```java
List<FiguritaIntercambiable> todasLasRepetidas = repositorioPerfiles.buscarTodos().stream()
    .flatMap(u -> u.getColeccion().getRepetidas().stream())
    .collect(Collectors.toList());

int totalSubastasActivas = (int) repositorioSubastas.buscarTodos().stream()
    .filter(Subasta::estaActivo)
    .count();
```

Carga TODOS los perfiles + TODAS las subastas en memoria. Para una entrega académica funciona; para 10k usuarios se cae.

**PoC (`EstadisticasServiceImpl.obtenerEstadisticas`):**

```java
int totalUsuarios = repositorioUsuarios.contar();
List<FiguritaIntercambiable> todasLasRepetidas = repositorioIntercambiables.buscarTodos();
int totalSubastasActivas = repositorioSubastas.contarActivas();   // ← query, no scan
```

`contarActivas` en `RepositorioSubastasMongoDB`:

```java
LocalDateTime ahora = LocalDateTime.now();
Query consulta = new Query(
    Criteria.where("fecha_inicio").lt(ahora).and("fecha_cierre").gt(ahora)
);
return Math.toIntExact(mongoTemplate.count(consulta, Subasta.class));
```

Y `contarPorEstado` en `RepositorioPropuestasMongoDB` (aggregation pipeline):

```java
Aggregation agg = Aggregation.newAggregation(
    Aggregation.project().and("estado").slice(-1).as("ultimoEstado"),
    Aggregation.unwind("ultimoEstado"),
    Aggregation.group("ultimoEstado.valor").count().as("total")
);
```

> **Veredicto:** Para `contarActivas` y `contarPorEstado`, la PoC gana **claramente** — escala. Sigue pendiente bajar `buscarTodos()` de FiguritaIntercambiable a una agregación; eso es deuda compartida con la mergeada.

### 2.8 Bugs y deuda heredada

Estos están **en la base común** y persisten en ambas:

| Bug | PR #104 | PoC |
|---|---|---|
| `Subasta.agregarOferta` con lógica invertida (`>=` debería ser `<`) | Presente | Presente |
| `cargarCalificaciones()` no guarda Perfil después de agregar | Presente | Presente |
| `IDs duplicados en seed` (Juan y Matías ambos `Coleccion("3")`) | Presente | **Ausente** — PoC asigna ids `1`, `2`, `3`, `4` |
| `Coleccion.tieneFaltante` mediante `equals` | Usa `getId().equals` ✓ | Usa `.contains(figurita)` que depende de `equals` no implementado en Figurita ✗ |
| `RuntimeException` en lugar de excepción de dominio en `FiguritaIntercambiable` | Presente | Presente |

> Las dos bases comparten muchos bugs porque son la misma. La PoC corrigió el bug de ids duplicados pero introdujo un regresión con `.contains()`. La mergeada hizo lo opuesto.

### 2.9 Bugs propios de cada implementación

**Solo en PR #104:**
- Credenciales de Atlas en `application.properties` (commit history) — **crítico**.
- Queries con `@DBRef` mal armadas (`Criteria.where("autor").is(...)` vs. `"autor.$id"`).
- N+1 sistémico en `ServicioFigurita.buscarPerfil(fi.getPerfilId())`.
- `RepositorioPropuestasMongo.buscarPorId` lanza `RuntimeException` (rompe `ErrorHandler`).
- Pérdida de paginación en `ServicioPerfil.obtenerSugerencias` (devuelve `(0, 0, 0)`).
- Doble path de creación de usuario (`ServicioSesion.crearUsuario` plain text vs. `ServicioUsuario.registrar` BCrypt).

**Solo en PoC:**
- `Coleccion.tieneFaltante(figurita)` usa `List.contains(figurita)`. Como `Figurita` no implementa `equals/hashCode`, **siempre devuelve `false`**. La validación de duplicados queda desactivada → se pueden agregar la misma figurita N veces a faltantes.
- `Perfil.obtenerCalificacionMedia()` retorna `1.0` cuando no hay calificaciones (debería ser `0.0`).
- `RepositorioNotificacionesMongoDB.buscarPorUsuario(Perfil)` recibe un `Perfil` pero solo usa `usuario.getId()` — sería más limpio pasar solo el id desde el servicio.
- `InicializadorDeDatos` no tiene `@Profile("!test")` ni honra `APP_SEED_DATA` (declarada en `docker-compose.yml` pero no leída en código).
- `Figurita.tipo` se usa en `RepositorioFiguritasMongoDB.buscarConFiltros` pero `Figurita` **no tiene un campo `tipo`** — la query nunca matchea, pero no falla porque `MongoTemplate` ignora paths inexistentes en lookups.
- **No hay tests para los repositorios Mongo** — solo `EnMemoria`. Esto significa que los bugs del Mongo path no se detectan en CI.
- Tests de servicios usan Mockito (rápidos) pero no cubren la integración real con Mongo.

### 2.10 Configuración y operación

**Mergeada:**
```properties
spring.data.mongodb.uri=mongodb+srv://tacs:tacs123*@cluster1.0ymras5.mongodb.net/appFiguritas
```
- URI completa con credenciales en el repo.
- Sin Docker.
- Sin distinción dev/prod.
- Sin Makefile.

**PoC:**
```properties
spring.data.mongodb.uri=mongodb://root:root@localhost:27017/tacs?tls=false&authSource=admin
cors.allowed-origin=${CORS_ALLOWED_ORIGIN:http://localhost:5173}
spring.profiles.active=mongodb
```

Aún hay `root:root` en el `.properties`, **pero**:
- Es para dev local con Docker Compose, no Atlas en la nube.
- `docker-compose.yml` sobre-escribe con `SPRING_DATA_MONGODB_URI=mongodb://root:root@mongodb:27017/tacs?authSource=admin` y `docker-compose.dev.yml` lo lee del `.env`.
- El `.env` debería estar en `.gitignore` (verificar).

Tiene además:
- `Makefile` con `make build`, `make up`, `make down`, `make dev`, `make test`, etc.
- `docker-compose.dev.yml` con Compose Watch para HMR del frontend.
- Multi-stage `Dockerfile` que aprovecha cache de capas (`dependencies → dev → build → runtime`) y crea usuario sin privilegios en el runtime.
- Nginx en frontend con usuario sin privilegios.
- Healthchecks en backend y MongoDB.

> **Veredicto:** La PoC tiene una infraestructura **considerablemente más madura**. El cluster Atlas de la mergeada hay que rotarlo de urgencia; la PoC es agnóstica al provider y se puede deployar contra Atlas, Mongo on-prem, etc., con solo cambiar `SPRING_DATA_MONGODB_URI`.

### 2.11 Tests

**Mergeada:**
- Reescribe TODOS los tests para usar `MongoTestBase` con `@SpringBootTest + flapdoodle`.
- `@AfterEach` dropea collections.
- Lento (cada test class arranca Spring Context + Mongo embedded), pero cubre el path real.

**PoC:**
- Mantiene los tests `EnMemoria` (rápidos, no requieren Mongo).
- `EstadisticasServiceImplTest` usa Mockito → no toca Mongo, súper rápido.
- **No hay tests sobre las implementaciones Mongo** — gap importante.

> **Veredicto:** Hay un trade-off. La mergeada cubre el path real pero tarda en correr. La PoC tiene una suite rápida pero ciega para Mongo. **La PoC necesita sumar tests de integración Mongo** (puede aprovechar flapdoodle igual que la mergeada, o testcontainers).

### 2.12 Datos del seed (`InicializadorDeDatos`)

**Mergeada:**
- `@Profile("!test")` — no corre en tests.
- IDs duplicados en Coleccion (Juan y Matías ambos `"3"`).
- `cargarCalificaciones()` no persiste.
- `cargarFiguritasExtra()` tiene un `//TODO: intercambiables.guardar(...)` — la línea está comentada (no carga las intercambiables extra).

**PoC:**
- Sin `@Profile`, corre siempre. **Genera duplicados en cada arranque** salvo que el `_id` esté fijo (lo está, pero queda dependiente de eso).
- IDs únicos (`1`, `2`, `3`, `4`).
- `cargarCalificaciones()` tampoco persiste (mismo bug).
- `cargarFiguritasExtra()` SÍ guarda las `intercambiables` (descomentó el TODO).
- `APP_SEED_DATA` declarada en compose pero no consumida → la "feature" de skip-seed no funciona.

> **Veredicto:** Ambas tienen el mismo bug central (no persistir Calificaciones agregadas en `cargarCalificaciones`). La PoC corrigió los ids duplicados y descomentó las intercambiables extra. Falta cablear `APP_SEED_DATA` (ej. `@ConditionalOnProperty("app.seed-data")`).

### 2.13 Documentación

**Mergeada:** sin doc específica de la migración. README sin cambios. Doc de auth/errors viene en PR #108.

**PoC:** README actualizado con secciones de stack, comandos make, endpoints, ejemplos curl. Detallado y útil para onboarding.

> **Veredicto:** PoC. Cumple con la guía del proyecto de actualizar README cuando hay cambios de contrato/infra.

---

## 3. Resumen de calidad por dimensión

> Escala: ✅ bien resuelto · ⚠️ tiene issues · ❌ roto/falta · ➖ no aplica

| Dimensión | Mergeada | PoC |
|---|---|---|
| Scope disciplinado | ❌ | ✅ |
| Queries con `@DBRef`/`@DocumentReference` correctas | ❌ | ✅ |
| Coexistencia memoria/mongodb | ❌ | ✅ |
| Documentación / Docker / Makefile | ❌ | ✅ |
| Spring Boot al día | ⚠️ (3.2.5) | ✅ (3.4.5) |
| Estadísticas con agregaciones | ❌ | ✅ |
| Configuración por env vars / no leakea secretos | ❌ | ⚠️ (default tiene credenciales aunque sea para dev) |
| Tests sobre Mongo | ✅ | ❌ |
| Tests rápidos | ⚠️ (lentos) | ✅ |
| Seed idempotente | ⚠️ (`!test` pero IDs colisionan) | ⚠️ (sin guard de profile/property) |
| Auth (Usuario, password hash) | ⚠️ (presente pero con bugs) | ➖ (fuera de scope) |
| `Coleccion.tieneFaltante` correcta | ✅ | ❌ |
| Cobertura test del path real Mongo | ✅ (flapdoodle) | ❌ |
| Embedded vs Document coherente | ⚠️ | ✅ |
| Domain methods con `@PersistenceCreator` | ❌ | ✅ |

---

## 4. Recomendaciones combinadas

Si el objetivo es **converger** ambas ramas en una sola implementación, lo razonable es **tomar la base de la PoC** (estrategia profile-switchable, `@DocumentReference`, `@PersistenceCreator`, Docker/Makefile, repositorios duales) y **sumarle de la mergeada**:

### De la PoC (mantener tal cual)
1. ✅ Paquetes `repositories/memoria/` y `repositories/mongodb/` con `@Profile`.
2. ✅ `@DocumentReference` (no `@DBRef`).
3. ✅ `@AllArgsConstructor(onConstructor_ = @PersistenceCreator)` en entidades con varios constructores.
4. ✅ Docker Compose prod/dev + Makefile + README.
5. ✅ `EstadisticasServiceImpl` usando `contarPorEstado()` y `contarActivas()`.
6. ✅ CORS con `@Value`.
7. ✅ Tests rápidos con Mockito para los servicios.

### De la mergeada (incorporar)
1. ✅ Tests de integración Mongo con flapdoodle (`MongoTestBase`). Adaptar a los repos `mongodb/`.
2. ⚠️ Entidades `Usuario` y `Calificacion` con campos para auth, pero con `@DocumentReference` (no `@DBRef`).
3. ✅ Hashing BCrypt en registro de usuarios.
4. ✅ Filtros movidos a `dto.filtros` (más limpio).

### Bugs a corregir antes de mergear cualquier cosa
1. **Rotar credenciales Atlas + mover URI a env var** (heredado de mergeada).
2. **Eliminar `Rol` de `UsuarioRequest`** (heredado).
3. **`Subasta.agregarOferta`** — corregir lógica invertida (en ambas).
4. **`Coleccion.tieneFaltante`** — usar comparación por id (regression en PoC).
5. **`InicializadorDeDatos`**: cablear `APP_SEED_DATA` + persistir `cargarCalificaciones`.
6. **`Perfil.obtenerCalificacionMedia()`** — `orElse(0.0)` no `1.0`.
7. **Quitar `Figurita.tipo` filter** en `RepositorioFiguritasMongoDB.buscarConFiltros` (campo inexistente).
8. **Sumar `equals/hashCode` en entidades clave** (`Figurita`, `Perfil`, `Usuario`) o documentar por qué no.

### Estructura propuesta de PRs si se decide rehacer
1. **PR-A: Infra** — Dockerfile, docker-compose, Makefile, README. Sin tocar código Java.
2. **PR-B: Persistencia (PoC mejorada)** — repos `mongodb/` con `@DocumentReference`, profile switch, tests de integración Mongo, bugfixes 3-7 anteriores.
3. **PR-C: Auth** — `Usuario`, `Calificacion`, BCrypt, JWT, filter. Sin meter rename ni reorganización de paquetes.
4. **PR-D: Rename `*Service → Servicio*`** — solo rename, idealmente automatizado.
5. **PR-E: Eliminar interfaces de servicio** (si se decide hacerlo) — separado del rename.

Cada uno reviewable en menos de 30 minutos.

---

## 5. Apéndice: comandos para reproducir la comparación

```bash
# Diff entre PoC y mergeada (rangos)
git diff feature/poc-mongodb..8b5850d -- backend/src/main/java/app/repositories/

# Archivos solo en una rama
git diff --name-status feature/poc-mongodb..8b5850d | grep -E "^[AD]"

# Comparar entidad puntual
diff <(git show feature/poc-mongodb:backend/src/main/java/app/model/entities/Perfil.java) \
     <(git show 8b5850d:backend/src/main/java/app/model/entities/Perfil.java)

# Buscar usos de @DBRef vs @DocumentReference
git grep -n "@DBRef" 8b5850d -- backend/
git grep -n "@DocumentReference" feature/poc-mongodb -- backend/
```

---

## 6. Conclusión

La PoC (`feature/poc-mongodb`) tiene **decisiones arquitectónicas mejores** en casi todas las dimensiones que evalué (anotaciones, estructura de paquetes, configuración, estadísticas, infraestructura, documentación). La mergeada (PR #104) tiene **más cobertura de tests sobre Mongo** y agregó las entidades necesarias para auth, pero a un costo alto en scope mezclado y bugs introducidos por queries mal armadas con `@DBRef`.

Para producción, la PoC necesita:
1. Tests de integración Mongo.
2. Las entidades de auth (Usuario con password, Calificacion).
3. Bugfixes puntuales (§4).

Para producción, la mergeada necesita:
1. Rotación de credenciales y limpieza del repo.
2. Reescritura sistémica de queries con `@DBRef`.
3. Resolver los bugs documentados en `analisis-pr-104-persistencia.md` (~24 críticos).

**Subjetivamente, si tuviera que elegir una base para llevar adelante, partiría de la PoC y la enriquecería**, no al revés. El costo de mover las correcciones de la mergeada a la PoC es menor que el costo de arreglar los problemas estructurales de la mergeada (especialmente los queries con DBRef).
