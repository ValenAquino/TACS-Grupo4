# Análisis PR #104 — Persistencia en MongoDB

> **Rango analizado:** `efa4da1..8b5850d` (rama remota `developer`, no mergeada localmente)
> **Merge commit:** `8b5850d Merge pull request #104 from LucasFis/persistencia`
> **Tamaño:** 118 archivos, +3.364 / −2.962 líneas

---

## 1. Resumen ejecutivo

El PR migra **todos** los repositorios `*EnMemoria` a implementaciones MongoDB, agregando además entidades nuevas (`Usuario`, `Calificacion`), repositorios asociados y soporte para colecciones embebidas (`flapdoodle`) en tests. Aprovecha el cambio para hacer un rename masivo (`*Service` → `Servicio*`, `*Controller` → `Controlador*`) y eliminar las interfaces de servicio (`IPerfilService`, etc.).

| Métrica | Valor |
|---|---|
| Nuevas dependencias | `spring-boot-starter-data-mongodb`, `flapdoodle.embed.mongo.spring3x`, `jjwt-{api,impl,jackson}`, `spring-security-crypto` |
| Repositorios Mongo nuevos | 8 (`Perfiles`, `Colecciones`, `Figuritas`, `Propuestas`, `Subastas`, `Notificaciones`, `Usuarios`, `Calificaciones`) |
| Entidades anotadas `@Document` | `Perfil`, `Coleccion`, `Figurita`, `Propuesta`, `Subasta`, `Usuario`, `Calificacion`, `Notificacion` |
| Cambios estructurales | Rename masivo de controladores/servicios; filtros movidos de `model.entities.filtros` a `dto.filtros` |
| Tests reescritos | 16 (controllers + repos + services), nueva `MongoTestBase` |

### Aspectos positivos

- **Estructura limpia interfaz/impl** para repositorios — facilita el swap entre Mongo y in-memory en tests si se quisiera.
- **`MongoTestBase`** con `@AfterEach` que dropea collections — buen aislamiento entre tests.
- **`flapdoodle`** evita depender de Mongo real para CI — bien elegido.
- **`PaginaResultado<T>`** como `record` reutilizable con `mapearA` — buena abstracción.
- **`spring.data.mongodb.uri`** externalizable (en principio).
- **Pipelines de agregación** para sugerencias y búsquedas paginadas — evita cargar todo en memoria para queries específicas (aunque hay otras que sí lo hacen, ver §3).
- **`application-test.properties`** con versión de Mongo embebido fijada — reproducible.

---

## 2. Vulnerabilidades de seguridad

### 2.1 🔴 Credenciales de MongoDB hardcoded en el repo

`backend/src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://tacs:tacs123*@cluster1.0ymras5.mongodb.net/appFiguritas
```

**Impacto:** cualquiera con acceso al repo (incluyendo viejas branches, forks o bots que indexan GitHub público) tiene credenciales válidas para escribir/leer/borrar toda la base. El password está en el commit history y no se invalida con un revert.

**Acción:**
1. **Rotar la contraseña del usuario `tacs` en Atlas inmediatamente.**
2. Mover la URI a variable de entorno (`SPRING_DATA_MONGODB_URI`) y dejar el `.properties` con un placeholder o sin la línea.
3. Agregar `application*.properties` con secretos al `.gitignore` y crear `application.properties.example`.
4. Reescribir el historial del repo o usar [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/) para purgar el secreto del git history.

### 2.2 🔴 `UsuarioRequest` acepta `Rol` desde el cliente

`backend/src/main/java/app/dto/request/UsuarioRequest.java`:

```java
public class UsuarioRequest {
  String nombre;
  String contrasenia;
  Rol rol;   // ← viene del body, sin validar
}
```

`ControladorSesion.crearPerfil` (POST `/usuarios`) llama a `ServicioSesion.crearUsuario(body)` que persiste el `Rol` tal cual viene. Cualquier cliente puede registrarse como `ADMINISTRADOR`.

> Este PR no agrega autenticación. Pero el solo hecho de exponer `Rol` en el request, sumado al endpoint público, abre la puerta a privilege escalation desde el momento que se mergea persistencia.

**Acción:** eliminar el campo `rol` del `UsuarioRequest`. El registro debe forzar `Rol.USUARIO`; cualquier promoción a `ADMINISTRADOR` debe pasar por un endpoint protegido con autorización.

### 2.3 🔴 `ServicioSesion.crearUsuario` guarda contraseñas en plano

```java
public void crearUsuario(UsuarioRequest req) {
  Usuario usuario = new Usuario(null, req.getRol(), req.getNombre(), req.getContrasenia());
  this.repoSesion.guardar(usuario);
}
```

Sin hash, sin sal. La rama `LucasFis/persistencia` agregó `spring-security-crypto` (¡y existe en paralelo `ServicioUsuario.registrar` que **sí** usa `BCryptPasswordEncoder`!), pero `ServicioSesion.crearUsuario` (que es el que está cableado al endpoint `/usuarios`) no lo aprovecha. Resultado: hay dos paths de registro con comportamiento incompatible.

**Acción:** eliminar `ServicioSesion.crearUsuario` y dejar solo `ServicioUsuario.registrar`. Apuntar `/usuarios` a este último.

### 2.4 🟠 Seed file con contraseñas en plano

`InicializadorDeDatos`:

```java
new Usuario("u-1003", Rol.USUARIO, "juan_jose", "una contrasenia");
new Usuario("u-1000", Rol.USUARIO, "lucas_fis", "gordo123");
new Usuario("u-1001", Rol.USUARIO, "sofia_ape", "password");
new Usuario("u-1002", Rol.USUARIO, "mati_crim", "wordpass");
```

Una vez que ServicioSesion empiece a hashear (acción 2.3), nadie va a poder loguearse con estos usuarios porque la base tiene hashes pero el seed escribe plano (o viceversa). Hay que decidir si el seed debe hashear o si se elimina por completo para producción.

**Acción:** en `InicializadorDeDatos` usar el mismo `PasswordEncoder` que el flujo de registro real.

---

## 3. Bugs funcionales

### 3.1 🔴 `Subasta.agregarOferta` — lógica invertida

```java
public void agregarOferta(Propuesta oferta) {
  boolean tieneCondicionesSolicitadas = !this.figuritasSolicitadas.isEmpty();
  boolean noOfertaLasSolicitadas = this.figuritasSolicitadas.stream()
      .noneMatch(fs -> oferta.getFiguritasOfrecidas().contains(fs));

  if (tieneCondicionesSolicitadas &&
      (noOfertaLasSolicitadas || oferta.getAutor().getCalificacionMedia() >= this.calificacionMinimaSolicitada)) {
    throw new BadRequestException("No se cumplieron las condiciones minimas");
  }
  ...
}
```

Dos errores:
1. **`>=`** debería ser **`<`**: actualmente lanza error cuando el usuario **alcanza** la calificación mínima (lo contrario de lo deseado).
2. La conjunción **`||` con `noOfertaLasSolicitadas`** rechaza una oferta válida (ofreció lo pedido) solamente porque el autor tiene calificación suficiente. La condición debería ser **`&&`** o estar separada en dos validaciones.

**Lógica correcta esperada:**
```java
if (tieneCondicionesSolicitadas) {
  if (noOfertaLasSolicitadas) {
    throw new BadRequestException("La oferta no incluye las figuritas solicitadas");
  }
  if (oferta.getAutor().getCalificacionMedia() < this.calificacionMinimaSolicitada) {
    throw new BadRequestException("Calificación insuficiente");
  }
}
```

### 3.2 🔴 `RepositorioPropuestasMongo` — query incompatible con `@DBRef`

```java
public List<Propuesta> buscarPorAutorId(String perfilId) {
  Query query = new Query();
  query.addCriteria(Criteria.where("autor").is(perfilId));   // ← guarda DBRef, busca String
  return this.mongoTemplate.find(query, Propuesta.class);
}
```

`Propuesta.autor` está mapeado con `@DBRef`, por lo que en Mongo se guarda como `{"$ref": "perfiles", "$id": "1000"}`. Buscar `Criteria.where("autor").is("1000")` no matchea ese subdocumento. La query correcta es `Criteria.where("autor.$id").is(perfilId)`.

Mismo bug en `buscarPorDestinatarioId`. Las dos APIs probablemente devuelven listas vacías en producción.

> El PR de autenticación (#108) "corrige" parcialmente: usa `"autor.id"`, que **tampoco** es correcto. Ver análisis de PR #108 §3.5.

### 3.3 🟠 `RepositorioNotificacionesMongo.buscarPorPerfil` — pasa objeto completo como criterio

```java
public List<Notificacion> buscarPorPerfil(Perfil perfil) {
  Query query = new Query();
  query.addCriteria(Criteria.where("perfil").is(perfil));   // ← serializa el Perfil completo
  return this.mongoTemplate.find(query, Notificacion.class);
}
```

`perfil` es `@DBRef`. La igualdad debería hacerse contra el `$id`:

```java
Criteria.where("perfil.$id").is(perfil.getId())
```

### 3.4 🔴 IDs duplicados en `InicializadorDeDatos`

```java
// línea ~140
Coleccion coleccionJuan = new Coleccion("3");
...
// línea ~195
Coleccion coleccionMatias = new Coleccion("3");   // ← mismo id
```

Mongo no falla, sobrescribe. La colección de Matías reemplaza la de Juan (o vice-versa según orden). Resultado: los seeds se pisan silenciosamente.

### 3.5 🔴 `cargarCalificaciones()` no persiste

```java
private void cargarCalificaciones() {
  Perfil lucas  = perfiles.buscarPorId("1000");
  ...
  lucas.getCalificaciones().add(new Calificacion("C-1", sofia, 5, "Excelente", "2000", INTERCAMBIO));
  lucas.getCalificaciones().add(new Calificacion("C-2", matias, 4, "Todo bien", "2002", INTERCAMBIO));
  // ... 9 calificaciones más
  // ← falta perfiles.guardar(lucas), perfiles.guardar(sofia), etc.
  // ← falta calificaciones.guardar(...) para cada Calificacion
}
```

Todas las calificaciones agregadas en este método se pierden cuando la transacción termina y los objetos en memoria se descartan. Además, el `add()` directo bypassa `agregarNuevaCalificacion()`, por lo que `cantidadCalificaciones` y `calificacionMedia` quedan **desactualizados aunque se persistiera**.

### 3.6 🔴 `ServicioPerfil.obtenerNotificaciones` confunde `userId` con `perfilId`

```java
public List<NotificacionesDto> obtenerNotificaciones(String userId) {
  Perfil perfil = repositorioPerfiles.buscarPorId(userId);   // ← busca por perfilId, no userId
  return this.repositorioNotificaciones.buscarPorPerfil(perfil)...
}
```

El parámetro se llama `userId` pero se usa con `buscarPorId` (que espera `perfilId`). Si el cliente pasa el `usuarioId` real, la query falla con `NotFoundException` aunque el usuario exista.

Mismo problema en `obtenerIntercambiablesPerfil`. Hay una doble confusión: dentro del método se llama `buscarPorId(userId)` (espera perfilId) y luego `buscarIntercambiablesPorPerfilId(userId)` reforzando la ambigüedad.

### 3.7 🟠 `ServicioPerfil.obtenerSugerencias` rompe la paginación

```java
PaginaResultado<Sugerencia> sugerencias = ...generarSugerencias(...);
// TODO: sigue en implementacion
return new PaginaResultado<>(
    sugerencias.contenido().stream().map(SugerenciaDto::new).toList(),
    0, 0, 0);   // ← descarta totalElementos, totalPaginas y página actual
```

El cliente recibe `cantidadDeElementos: 0`, `cantidadDePaginas: 0`, `numero: 0` aunque el contenido tenga elementos. UI de paginación queda inoperante.

### 3.8 🟠 Bypass sistemático de métodos de dominio

`ServicioSubasta.rechazarOferta`, `cancelarSubasta`, `cerrarSubasta` y `seleccionarOferta`:

```java
propuesta.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.RECHAZADO));
```

en lugar de `propuesta.rechazar(perfilCorrespondiente)`. Esto:
1. Bypasea `validarPendiente()` → permite rechazar dos veces o rechazar una propuesta ya aceptada.
2. Bypasea `validarUsuarioDestino()` → no chequea que quien rechaza sea el dueño de la subasta.

`Coleccion.agregarRepetida()` también es bypaseable en cualquier servicio que tenga acceso a `getRepetidas().add(...)` (que es público vía `@Setter`).

### 3.9 🟠 `ServicioColeccion.agregarRepetida` y `agregarFaltante` no notifican guardar la Coleccion antes de notificar

El método guarda la colección y luego notifica interesados. Si el `notificarInteresados` falla (por ej. un perfil con datos rotos), las notificaciones quedan inconsistentes con el estado. No es bloqueante pero conviene separar (al menos) en transacciones lógicas.

### 3.10 🟠 `ServicioSubasta.crearSubasta` — `NullPointerException` si `duracionEnHoras` es null

```java
LocalDateTime fechaFin = fechaInicio.plusHours(duracionEnHoras.longValue());
```

No hay validación. Si el cliente omite el campo, NPE → 500.

### 3.11 🟠 `ServicioSubasta.obtenerOferta` — `Optional.get()` sin presencia garantizada

```java
private Propuesta obtenerOferta(Subasta subasta, String userId) {
  return subasta.getOfertas().stream()
      .filter(p -> p.getAutor().getUsuario().getId().equals(userId))
      .findFirst()
      .get();   // ← NoSuchElementException si no encuentra
}
```

Si el listado de subastas se desincroniza (por ejemplo: la subasta fue devuelta por `buscarDondeParticipa` por algún motivo de cache) y la oferta no aparece, explota.

### 3.12 🟠 `RepositorioPropuestasMongo.buscarPorId` lanza `RuntimeException`

```java
throw new RuntimeException("Propuesta no encontrada");
```

Mientras que el resto del proyecto usa `NotFoundException` (mapeada a 404 en `ErrorHandler`). Esto va a producir 500.

### 3.13 🟠 `Calificacion.transactionId` y `tipoTransaccion` sin integridad referencial

`transactionId` es un `String` libre. No hay forma de validar que la propuesta/subasta referida exista. Tampoco hay índice ni unicidad sobre `(autor.id, transactionId, tipoTransaccion)` que es lo que efectivamente identifica una calificación única (la validación está implementada en `ServicioPerfil.agregarCalificacion` por lectura previa, pero es propensa a race conditions).

---

## 4. Problemas de modelo / persistencia

### 4.1 🟠 Uso indiscriminado de `@DBRef`

Casi todas las entidades referencian a otras con `@DBRef` (incluso listas: `List<Figurita>` faltantes, `List<Calificacion>` calificaciones, `List<Figurita>` figuritasOfrecidas, etc.). Esto produce:

1. **N+1 queries**: cargar un `Perfil` con 50 calificaciones dispara 50 reads adicionales.
2. **Falta de atomicidad**: si el referenciado se borra, queda referencia colgante.
3. **Performance pésima en agregaciones**: hay que hacer `$lookup` manual en cada pipeline (como ya pasa en `RepositorioColeccionesMongo`).

**Acción:** evaluar embebido vs. referenciado según patrón de acceso. Para listas pequeñas y que siempre se cargan juntas (`mediosDeContacto`), embebido. Para listas grandes o que se acceden por separado, referencia + colección dedicada.

### 4.2 🟠 `Coleccion.repetidas` es embedded pero `Coleccion.faltantes` es DBRef

```java
@DBRef
private List<Figurita> faltantes = new ArrayList<>();

private List<FiguritaIntercambiable> repetidas = new ArrayList<>();   // ← embedded
```

Inconsistencia. `FiguritaIntercambiable` tiene un `@DBRef Figurita figurita` adentro, así que termina mezclando dos esquemas. Decidir uno y aplicarlo de manera consistente.

### 4.3 🟠 Falta de índices

Ninguna entidad tiene `@Indexed`. Las queries por `nombre` (login), `autor.$id`, `destinatario.$id`, `usuario.id` son full scans. Para pocos miles de docs no se nota, pero a partir de ahí va a colapsar.

**Acción mínima:**
- `Usuario.nombre` → unique
- `Perfil.usuario.$id` → unique
- `Propuesta.autor.$id`, `Propuesta.destinatario.$id` → index
- `Subasta.autor.$id`, `Subasta.fechaCierre` → index
- `Calificacion.autor.$id`, `Calificacion.transactionId` → index

### 4.4 🟠 `RepositorioUsuariosMongo` incompleto

Solo expone `guardar(Usuario)`. No hay `buscarPorId`, `buscarPorNombre` (lo agrega el PR #108), ni nada que permita listarlos. Para producción se va a necesitar al menos `buscarPorNombre` (unique) y exponerlo en la interfaz.

### 4.5 🟠 `RepositorioCalificacionesMongo` incompleto

Solo `guardar`. No hay forma de listar las calificaciones de un perfil sin recorrer el `Perfil.calificaciones` que es una `List<@DBRef>` (N+1 nuevamente). El PR #108 agrega `buscarPorPerfil`.

### 4.6 🟠 `RepositorioFiguritasMongo.buscarConFiltros` retorna `List` sin paginar

```java
public List<Figurita> buscarConFiltros(FiguritasFiltro filtros) {
  Query query = new Query();
  if (filtros.id() != null) { ... }
  ...
  return this.mongoTemplate.find(query, Figurita.class);   // ← sin skip/limit
}
```

El cliente no puede paginar este endpoint. Si la colección crece, el response se hace inmanejable.

### 4.7 🟠 Falta de transacciones

`ServicioPerfil.agregarCalificacion` hace dos `guardar()` separados (la `Calificacion` y el `Perfil`). Si Mongo se cae entre uno y otro, queda inconsistente. Idem `ServicioUsuario.registrar` después del PR #108 (crea Usuario + Coleccion + Perfil sin transacción, ver análisis correspondiente).

Para MongoDB las transacciones requieren replica set. Atlas free tier las soporta. Vale la pena envolver flujos críticos con `@Transactional`.

### 4.8 🟠 `Usuario` con `@AllArgsConstructor` + constructor manual

```java
@AllArgsConstructor
public class Usuario {
  @Id private String id;
  private Rol rol;
  private String nombre;
  private String contrasenia;

  public Usuario(String nombre, String contrasenia, Rol rol) { ... }   // ← orden distinto
}
```

Dos constructores con tipos solapados (`String, String, Rol` vs. `String, Rol, String, String`) son trampa: una refactor de Lombok o un cambio de orden de campos se cuela como bug silencioso. Mejor usar `@Builder` o un único factory explícito.

---

## 5. Diseño y arquitectura

### 5.1 🟠 Rename masivo dentro del PR de persistencia

El PR mezcla:
- Migración a Mongo (el objetivo declarado)
- Rename `*Controller → Controlador*` y `*Service → Servicio*`
- Eliminación de interfaces de servicio (`IPerfilService`, `IColeccionService`, etc.)
- Movimiento de filtros entre paquetes

Esto hace muy difícil reviewar el PR. Idealmente, esos cambios deberían ir en PRs separados.

### 5.2 🟠 Pérdida del nivel de abstracción de servicios

Antes existían `IPerfilService` ↔ `PerfilService`, ahora solo `ServicioPerfil`. Para esta etapa no es crítico pero pierde testabilidad (no hay sustituibilidad sin Mockito) y dificulta swap por implementaciones alternativas. Es discusión válida — solo dejar registrado que se hizo.

### 5.3 🟠 `ControladorSesion` mezcla responsabilidades

```java
@RestController
public class ControladorSesion {
  private final ServicioEstadisticas estadisticasService;
  private final ServicioSesion sesionService;

  @PostMapping("/usuarios") public ... crearPerfil(...) {...}
  @GetMapping("/administrador/estadisticas") public ... obtenerEstadisticas() {...}
}
```

Un controlador que crea usuarios y obtiene estadísticas de admin no tiene cohesión. Mover `/administrador/estadisticas` a un `ControladorAdministrador` y dejar `ControladorSesion` (post-PR #108) solo con sesión.

### 5.4 🟠 Inconsistencia: `userId` vs `perfilId`

A lo largo del código aparecen indistintamente `userId`, `perfilId`, `userAutorId`, `perfilDestinoId`, y a veces los métodos llaman a `buscarPorUsuarioId` cuando recibieron `perfilId`, o viceversa.

Ejemplo: `ControladorPerfil.obtenerPerfil(@PathVariable String user_id)` → `perfilService.obtenerPerfil(user_id)` → internamente usa `buscarPorUsuarioId(userId)` (que es correcto si el path es realmente un userId). Pero `/perfil/{user_id}/notificaciones` termina en `buscarPorId(userId)` (espera perfilId). Inconsistencia.

**Acción:** elegir un identificador canónico (userId) y derivar el resto en el backend. El frontend nunca debería tener que conocer el `perfilId`.

### 5.5 🟠 `@Getter` en `Usuario` expone la contraseña

`Usuario` tiene `@Getter` de Lombok. `getContrasenia()` se serializa en JSON salvo que se filtre explícitamente. Si en algún endpoint se serializa un `Usuario` (por ejemplo dentro de un `Perfil` via `@DBRef`), el hash de la contraseña termina en el response.

**Acción:** anotar `Usuario.contrasenia` con `@JsonIgnore`, o exponer un `UsuarioDto` que omita el campo.

### 5.6 🟠 `ErrorHandler` muy escueto

```java
@RestControllerAdvice
public class ErrorHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFound(NotFoundException ex) {...}

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleBadRequest(BadRequestException ex) {...}
}
```

No maneja:
- `PropuestaException` (extiende `RuntimeException`) → 500
- `FiguritaDuplicadaException` → 500
- `MethodArgumentNotValidException` (validaciones de Spring) → 500 sin mensaje útil
- `HttpMessageNotReadableException` (body inválido) → 500

Además devuelve `ResponseEntity<String>`, no JSON estructurado. Cualquier 500 expone el body con stacktrace (default de Spring).

> Esto se mejora en el PR #108, pero queda pendiente cubrir las excepciones de dominio listadas.

### 5.7 🟠 `ServicioEstadisticas.obtenerEstadisticas` no escala

```java
List<FiguritaIntercambiable> todasLasRepetidas = repositorioPerfiles.buscarTodos().stream()
    .flatMap(u -> u.getColeccion().getRepetidas().stream())
    .collect(Collectors.toList());
```

Carga **todos** los perfiles en memoria, hace `flatMap` en Java. Con 100k usuarios esto no termina. La estadística debería ser una agregación en Mongo:

```js
db.colecciones.aggregate([
  { $unwind: "$repetidas" },
  { $group: { _id: null, total: { $sum: 1 } } }
])
```

Idem `totalSubastasActivas` (`buscarTodos().filter(estaActivo)`) — debería ser query con `fechaCierre > now()` y `count`.

Hay un **import duplicado** en este servicio: `import app.repositories.RepositorioPerfiles;` aparece dos veces. Maven compila pero queda como ruido.

### 5.8 🟠 `ServicioFigurita.buscarPerfil` causa N+1

```java
List<FiguritaIntercambiableDto> contenido = paginaRepo.contenido().stream()
    .map(fi -> new FiguritaIntercambiableDto(fi, buscarPerfil(fi.getPerfilId())))
    .toList();
```

Una query a `RepositorioPerfiles` por cada figurita en la página. Si `tamanioPagina=40`, son 40 queries adicionales.

**Acción:** hacer un `findAll(byId in [...])` en bulk antes del mapeo, o incluir el perfil en el pipeline de agregación que arma la figurita.

### 5.9 🟠 `ServicioColeccion` con campo mal nombrado

```java
private final RepositorioPerfiles repositorioUsuarios;
```

`repositorioUsuarios` es realmente un `RepositorioPerfiles`. Pasos siguientes leen `this.repositorioUsuarios.buscarPorFiguritaFaltante(figurita)`. Confunde a cualquier lector.

### 5.10 🟠 `InicializadorDeDatos` no es idempotente

No hay `dropCollection` o `existsBy...` previo. En cada arranque:
- `figuritas.guardar(messi)` → como `messi.id = "ARG-10"`, sobrescribe pero gasta llamadas.
- Las calificaciones programáticas son objetos nuevos con ids fijos, también sobrescriben.
- Si en algún momento se agregan datos sin id explícito, se duplican.

Tampoco respeta el perfil `dev` vs `prod`. Está controlado por `@Profile("!test")` lo que significa que en prod también corre el seed. Mal.

**Acción:** usar `@Profile("dev")` o agregar guard `if (perfiles.contar() > 0) return;`.

---

## 6. Detalles menores / housekeeping

| # | Hallazgo | Archivo |
|---|---|---|
| 6.1 | TODO sin cerrar: `crearPerfil` crea con `usuario(null)` | `ServicioPerfil.crearPerfil` |
| 6.2 | Comentarios "Chequear si eso está bien" en código productivo | `Propuesta.java` |
| 6.3 | Comentario `//TODO` sobre línea comentada de `intercambiables.guardar(...)` | `InicializadorDeDatos.cargarFiguritasExtra` |
| 6.4 | `@Experimental` en `FiguritaIntercambiable.perfilId` (anotación de JFR, no es el `@Experimental` de Spring) | `FiguritaIntercambiable` |
| 6.5 | `RuntimeException` en `FiguritaIntercambiable.reservar/eliminarReserva/cambioConcretado` — usar excepción de dominio | `FiguritaIntercambiable` |
| 6.6 | `ServicioSubasta.mejorarOfertaEnSubasta` usa `nuevas_figuritas` (snake_case en Java) | `ServicioSubasta` |
| 6.7 | `application.properties` con `logging.level...=TRACE` en main resources | `application.properties` |
| 6.8 | `spring.jackson.property-naming-strategy=SNAKE_CASE` — afecta el mapeo de Mongo si los campos no coinciden | `application.properties` |
| 6.9 | Path variables en snake_case (`user_id`, `prop_id`) — funcionan pero rompen convención Java | controllers |
| 6.10 | `ControladorPropuesta` recibe `@RequestHeader String usuario_id` — identidad por header sin auth | `ControladorPropuesta` (resuelto en #108) |
| 6.11 | `SugerenciasFiltro` no valida `paginaActual`/`limite` null → NPE en repo | `SugerenciasFiltro`, `RepositorioPerfilesMongo` |
| 6.12 | `Repetidas<T>` con `@JsonUnwrapped` mezcla campos de pagina y publicadas/disponibles a nivel root | `Repetidas` |
| 6.13 | `RepositorioCalificacionesMongo` solo guarda. Se necesita lectura paginada (lo agrega #108) | `RepositorioCalificacionesMongo` |

---

## 7. Cobertura de tests

- Buen aislamiento con `MongoTestBase` + `@AfterEach`.
- `flapdoodle` permite tests con Mongo embebido. **Caveat:** los tests son `@SpringBootTest`, levantan el contexto completo → lentos.
- Faltan tests:
  - Validaciones de query/criteria en repositorios Mongo (especialmente las que tienen los bugs §3.2 y §3.3).
  - `InicializadorDeDatos` (no tiene test).
  - Edge case `Subasta.agregarOferta` con calificación mínima (descubriría §3.1).
  - `cargarCalificaciones` no se ejecuta en tests, no detecta §3.5.

---

## 8. Plan de acción priorizado

> Ordenado por: 1) impacto en seguridad, 2) impacto funcional, 3) deuda técnica.

### Crítico (hacer YA)

1. **Rotar credenciales de Atlas y purgar `application.properties` del repo** (§2.1). Acompañar de `.env`/secret y `application.properties.example`.
2. **Eliminar `Rol` de `UsuarioRequest`** (§2.2).
3. **Unificar el flujo de creación de usuarios** (eliminar `ServicioSesion.crearUsuario` con password plano) (§2.3).
4. **Corregir `Subasta.agregarOferta`** — invertir `>=` por `<` y separar las dos condiciones (§3.1).
5. **Corregir queries de DBRef** en `RepositorioPropuestasMongo` y `RepositorioNotificacionesMongo` (§3.2, §3.3).

### Alto

6. **Corregir IDs duplicados en seed** (§3.4) y reemplazar la mezcla de `add()` directo + `agregarNuevaCalificacion()` (§3.5).
7. **Persistir las calificaciones en `cargarCalificaciones()`** o eliminar el método si es muerto (§3.5).
8. **Aclarar `userId` vs `perfilId`** en servicios — definir convención y aplicar consistentemente (§3.6, §5.4).
9. **Completar `ErrorHandler`** con `PropuestaException`, `FiguritaDuplicadaException`, `MethodArgumentNotValidException` y formato JSON (§5.6).
10. **Ampliar interfaces de `RepositorioUsuarios` y `RepositorioCalificaciones`** (§4.4, §4.5).

### Medio

11. **Agregar índices** en colecciones relevantes (§4.3).
12. **Embedded vs DBRef** — revisar caso por caso (§4.1, §4.2).
13. **Optimizar `ServicioEstadisticas`** con pipelines de agregación (§5.7).
14. **Resolver N+1 en `ServicioFigurita.buscarPerfil`** (§5.8).
15. **Idempotencia de `InicializadorDeDatos`** y restricción a profile `dev` (§5.10).
16. **Eliminar bypass de domain methods** en `ServicioSubasta.{rechazar,cancelar,cerrar}Oferta` (§3.8).

### Bajo

17. Rename del campo `repositorioUsuarios` en `ServicioColeccion` (§5.9).
18. `@JsonIgnore` en `Usuario.contrasenia` (§5.5).
19. Eliminar comentarios de TODO/Chequear (§6.1, §6.2, §6.3).
20. Mover el rename de `Servicio*`/`Controlador*` y la eliminación de interfaces a PRs separados en el futuro (§5.1, §5.2).
21. Quitar `logging.level=TRACE` de prod (§6.7).
22. Validar nulls en filtros (§6.11).

---

## 9. Apéndice: archivos clave

| Archivo | Líneas | Comentario |
|---|---|---|
| `RepositorioColeccionesMongo.java` | 280 | Pipeline complejo, bien estructurado pero verbose |
| `RepositorioPerfilesMongo.java` | 190 | `generarSugerencias` es la query más compleja del proyecto |
| `InicializadorDeDatos.java` | 420 | Demasiado largo, contiene varios de los bugs listados |
| `MongoTestBase.java` | 44 | Buen pattern de base para tests Mongo |
| `application.properties` | 5 | **Tiene el secret** |
