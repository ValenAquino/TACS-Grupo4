# Análisis PR #108 — Autenticación con JWT

> **Rango analizado:** `8b5850d..56d0f17` (rama remota `developer`, no mergeada localmente)
> **Merge commit:** `56d0f17 Merge pull request #108 from LucasFis/autenticacion`
> **Tamaño:** 112 archivos, +5.591 / −1.475 líneas
> **Prerequisito:** asume aplicado el PR #104 (persistencia)

---

## 1. Resumen ejecutivo

El PR introduce autenticación mediante **JWT almacenado en cookie HttpOnly**, junto con un manejo centralizado de errores cross-stack. Cubre:

- **Backend:** `ServicioJwt`, `JwtFilter`, `ServicioSesion.login`, `ControladorSesion` (`/login`, `/yo`, `/sesion`), `ErrorResponse` uniforme.
- **Frontend:** `AuthProvider` (context React), `RutaProtegida`, interceptor de Axios para 401, vistas `Login` y `Registrar`, vistas de error (`acceso-denegado`, `servidor-caido`, `error-interno`).
- **Documentación:** `Manejo-de-autenticacion.md` (763 líneas), `Manejo-de-errores.md` (574 líneas).
- Refactor masivo: todos los controladores migran de `@RequestHeader String usuario_id` o `@PathVariable user_id` a `@CookieValue("token")` + `ServicioJwt.getPerfilId(token)`.

| Métrica | Valor |
|---|---|
| Nuevas clases backend | `JwtFilter`, `ServicioJwt`, `UnauthorizedException`, `UsuarioException`, `ErrorResponse`, `SesionDto`, `LoginRequest`, `PropuestasFiltro`, `PropuestasDto`, `CalificacionesDto` |
| Nuevas clases frontend | `userContext`, `errorContext`, `toastContext`, `RutaProtegida`, `Login`, `Registrar`, vistas de error, `api.js` |
| Endpoints públicos (sin JWT) | `/login`, `/usuarios`, `/figuritas`, `/ping`, `/sesion` |
| Tiempo de vida del token | 12 horas |
| Storage | Cookie HttpOnly + Secure + SameSite=None |

### Aspectos positivos

- **JWT en cookie HttpOnly** evita exposición a XSS para robo del token. Decisión correcta.
- **Manejo centralizado de errores** con `@RestControllerAdvice` + `ErrorResponse` estructurado (`status`, `message`, `errors`, `timestamp`).
- **Tests para `ServicioJwt`, `JwtFilter` y `ErrorHandler`** — los nuevos componentes tienen cobertura básica.
- **Documentación en `Manejo-de-autenticacion.md` y `Manejo-de-errores.md`** — ayuda al onboarding.
- **Frontend con Context API + interceptor** centraliza la lógica de sesión.
- **Reutilización del filtro** vía `shouldNotFilter` — patrón estándar y limpio.
- **Logout limpia la cookie con `maxAge(0)`** — correcto.
- **`Propuesta.cancelar`** se agrega como acción de dominio (la idea está bien, la implementación tiene un bug — ver §3.1).
- **Backend devuelve 401 → frontend dispara evento `logout`** — buena separación de responsabilidades.

---

## 2. Vulnerabilidades de seguridad

### 2.1 🔴 SECRET hardcoded en código fuente

`backend/src/main/java/app/servicios/ServicioJwt.java`:

```java
private final String SECRET =
    "jhggsddahjbujbyutydrrtweawqawq4456778689879864422345";
```

**Impacto:** cualquiera con acceso al repo puede:
1. Forjar tokens válidos arbitrariamente.
2. Crear un token con `rol: "ADMINISTRADOR"` y cualquier `perfilId`/`usuarioId`.
3. Impersonar cualquier usuario.

Con el repositorio público o accesible a un fork, **el sistema queda autenticación-equivalente a no tener autenticación**.

**Acción:**
1. Mover el secret a variable de entorno: `@Value("${jwt.secret}") private String secret;`
2. Generar una key fuerte (≥256 bits aleatorios) y rotarla.
3. Purgar el secret del git history (BFG, `git filter-repo`).
4. Considerar invalidar todos los tokens existentes después del rote (cambiando el secret).

### 2.2 🔴 Privilege escalation persistente: `Rol` desde el body

(Heredado de #104 §2.2). `ServicioUsuario.registrar` no rechaza un `Rol.ADMINISTRADOR` que venga del request:

```java
if (request.getRol() == null) {
  usuarioNuevo = new Usuario(..., Rol.USUARIO);
} else {
  usuarioNuevo = new Usuario(..., request.getRol());   // ← acepta lo que venga
}
```

`POST /usuarios` está fuera del filtro (`shouldNotFilter("/usuarios")`). El frontend (`usuarioService.crearUsuario`) envía `rol: "USUARIO"` deliberadamente, pero cualquier curl puede:

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"hacker","contrasenia":"x","rol":"ADMINISTRADOR"}'
```

…y queda como admin. Combinado con §2.1, la app no tiene defensa.

**Acción:** eliminar el campo `rol` de `UsuarioRequest`. La promoción a admin debe ir por un endpoint separado, protegido y auditado.

### 2.3 🔴 `/figuritas` excluido del filtro JWT

```java
protected boolean shouldNotFilter(HttpServletRequest request) {
  String path = request.getServletPath();
  return path.startsWith("/login")
      || path.startsWith("/usuarios")
      || path.startsWith("/figuritas")   // ← cualquiera puede listar/buscar
      || path.startsWith("/ping")
      || path.startsWith("/sesion");
}
```

`/figuritas` permite búsqueda libre por texto y filtros. Es información de negocio que debería estar detrás de auth. Si la intención era permitir un catálogo público, debería usarse una variante stripped del DTO (sin `perfilId`/datos de usuario asociados) — actualmente devuelve `FiguritaIntercambiableDto` con `getPerfil()`.

**Acción:** decidir si es público. Si sí, devolver un DTO de catálogo (sin perfiles). Si no, sacarlo del whitelist.

### 2.4 🔴 `path.startsWith()` permite escape de prefijo

```java
path.startsWith("/usuarios")    // matchea /usuarios, /usuarios/foo, /usuariosX, /usuarios-admin
path.startsWith("/login")       // matchea /login/test, /loginX, etc.
```

Si en el futuro se agrega `/usuariosAdmin` (sin slash), queda público sin querer. La aserción del test confirma el problema: `shouldNotFilter_rutasPublicas_devuelveTrue` con path `"/login/test"` devuelve `true`.

**Acción:** usar igualdad exacta o regex, idealmente `Set<String>` con `equals`.

### 2.5 🔴 Falta de autorización por rol (sin Spring Security)

El filtro **solo** valida que el token sea bien firmado y no expirado. No hay verificación de rol. Una vez logueado como `USUARIO`, se puede llamar a `/administrador/estadisticas`:

```java
@GetMapping("/administrador/estadisticas")
public ResponseEntity<EstadisticasDto> obtenerEstadisticas() {
  return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
}
```

No hay un `@PreAuthorize` ni un check en el método. El frontend tampoco lo cubre (`RutaProtegida` solo chequea `user` truthy, no rol).

**Acción:** introducir `@PreAuthorize("hasRole('ADMINISTRADOR')")` (requiere Spring Security) o un check explícito al inicio del método tipo `if (!"ADMINISTRADOR".equals(jwt.getRol(token))) throw new UnauthorizedException(...)`.

### 2.6 🔴 Falta de autorización por ownership

```java
@PostMapping("/{sub_id}/cancelar")
public ResponseEntity<Void> cancelarSubasta(@PathVariable String sub_id) {
  this.subastaService.cancelarSubasta(sub_id);   // ← no recibe el token
  return ResponseEntity.ok().build();
}
```

**Cualquier usuario logueado puede cancelar/cerrar/seleccionar/rechazar ofertas en CUALQUIER subasta.** Mismo problema en:
- `cerrarSubasta`
- `seleccionarOferta`
- `rechazarOferta`
- `mejorarOfertaEnSubasta`

`ServicioSubasta` tampoco recibe ni valida el `perfilId` del caller. Vulnerabilidad de IDOR (Insecure Direct Object Reference) sistémica.

**Acción:** pasar el `perfilId` (extraído del token) a todos esos endpoints y validar en el servicio:

```java
if (!subasta.getAutor().getId().equals(perfilId)) {
  throw new UnauthorizedException("Solo el autor puede cancelar la subasta");
}
```

### 2.7 🟠 Username enumeration vía `NotFoundException`

`ServicioSesion.login`:

```java
Usuario usuario = this.repoUsuario.buscarPorNombre(request.nombre());   // ← throws NotFoundException si no existe
boolean coincide = passwordEncoder.matches(request.contrasenia(), usuario.getContrasenia());
if (!coincide) throw new UsuarioException("Credenciales invalidas");
```

Si el usuario no existe → `NotFoundException` → 404 con body `{"status":404, "message":"Usuario no encontrado"...}`.
Si la contraseña es incorrecta → `UsuarioException` → 400 (porque `UsuarioException extends BadRequestException`) con `"Credenciales invalidas"`.

Distintos códigos y mensajes filtran si el usuario existe. Permite enumerar cuentas.

**Acción:** capturar `NotFoundException` en el login y relanzar como `UnauthorizedException("Credenciales inválidas")`, devolviendo siempre 401 con el mismo mensaje.

### 2.8 🟠 Cookie con `secure(true)` + frontend en `http://localhost:5173`

```java
ResponseCookie.from("token", token)
    .secure(true)
    .sameSite("None")
    ...
```

Combinación `Secure + SameSite=None` requiere HTTPS. En desarrollo local (Vite default `http://localhost:5173`), Chrome rechaza la cookie silenciosamente. El equipo seguramente lo notó y trabaja con flag o con HTTPS local, pero conviene parametrizar:

```java
.secure(env.equals("prod"))
.sameSite(env.equals("prod") ? "None" : "Lax")
```

### 2.9 🟠 No hay invalidación server-side de tokens (problema clásico JWT)

`/sesion DELETE` solo borra la cookie en el browser. Si un atacante capturó el token, lo puede usar las 12 horas restantes hasta expiración sin importar que el dueño haga logout.

**Mitigaciones posibles:**
- TTL corto (15 min) + refresh token.
- Lista negra de JTI (claim único por token) revisada en cada request.
- Versión de sesión en `Usuario` que invalida tokens anteriores cuando se incrementa.

### 2.10 🟠 Logging level TRACE en `application.properties`

```properties
logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping=TRACE
```

En producción, `TRACE` puede loggear headers (incluyendo cookies con el token). Bajar a `WARN` o `INFO`.

### 2.11 🟠 Sin rate limiting en `/login`

`POST /login` no tiene límite de intentos. Combinado con BCrypt (slow by design), un atacante puede simplemente esperar — no es DoS-proof pero sí ataque por fuerza bruta con accounts conocidos. Aún más relevante por §2.7 (enumeración).

---

## 3. Bugs funcionales

### 3.1 🔴 `Propuesta.validarUsuarioAutor` es copy-paste de `validarUsuarioDestino`

```java
private void validarUsuarioDestino(Perfil perfil) {
  if (!this.destinatario.getId().equals(perfil.getId())) {
    throw new PropuestaException("Solo el destinatario puede responder la propuesta");
  }
}

private void validarUsuarioAutor(Perfil perfil) {
  if (!this.destinatario.getId().equals(perfil.getId())) {   // ← debería ser this.autor.getId()
    throw new PropuestaException("Solo el destinatario puede responder la propuesta");
  }
}
```

El método `cancelar(Perfil perfil)` llama a `validarUsuarioAutor(perfil)` esperando validar que `perfil` es el autor de la propuesta. Pero efectivamente valida que sea el destinatario. **Resultado: el autor nunca puede cancelar su propia propuesta**. Solo el destinatario podría, pero el destinatario ya tiene `aceptar` y `rechazar`.

**Fix trivial:**
```java
private void validarUsuarioAutor(Perfil perfil) {
  if (!this.autor.getId().equals(perfil.getId())) {
    throw new PropuestaException("Solo el autor puede cancelar la propuesta");
  }
}
```

Y agregar un test que cubra el caso del autor cancelando.

### 3.2 🟠 `ServicioSesion.login` no maneja `NotFoundException` antes de matchear

Ya cubierto en §2.7. Hoy si el usuario no existe explota con NotFoundException en lugar de UnauthorizedException.

### 3.3 🟠 `Perfil.getIniciales()` puede tirar IndexOutOfBoundsException

```java
public String getIniciales() {
  return this.nombre.substring(0, 2).toUpperCase();   // ← nombre.length() < 2 → StringIndexOutOfBoundsException
}
```

Si un usuario se registra con `nombre = "A"`, `Perfil` se crea con ese nombre (no hay validación de longitud mínima) → cualquier endpoint que serialice `CalificacionDto` (que llama `getIniciales()`) revienta con 500.

**Fix:**
```java
public String getIniciales() {
  if (nombre == null || nombre.isBlank()) return "";
  return nombre.length() < 2 ? nombre.toUpperCase() : nombre.substring(0, 2).toUpperCase();
}
```

Idealmente, validar longitud mínima en el registro.

### 3.4 🟠 `CalificacionDto` NPE si autor está roto

```java
public CalificacionDto(Calificacion c) {
  this.autorId = c.getAutor().getId();      // ← NPE si autor es null
  this.iniciales = c.getAutor().getIniciales();
  ...
}
```

`Calificacion.autor` es `@DBRef`. Si el perfil fue borrado o la referencia se rompió, el getter devuelve `null` → NPE en cada request que liste calificaciones.

### 3.5 🔴 Queries Mongo aún incorrectas con DBRef

`RepositorioPropuestasMongo` "corrigió" parcialmente las queries pero las dejó mal:

```java
Criteria.where("autor.id").is(perfilId)     // ← debería ser "autor.$id"
Criteria.where("destinatario.id").is(perfilId)
```

`autor.id` no es el path correcto para un `@DBRef`. El path real es `autor.$id`. Esto sigue sin devolver resultados (o devuelve resultados incorrectos según versión de Spring Data).

`RepositorioCalificacionesMongo.buscarPorPerfil`:

```java
Criteria.where("autor.$id").is(perfilId)   // ← perfilId es String pero $id puede ser ObjectId
```

Si los ids de `Perfil` son strings ("1000"), funciona. Si son ObjectId autogenerados (cuando se crean por `registrar()`), `is(perfilId)` con un String va a fallar porque `$id` es ObjectId. Spring Data **no siempre** auto-convierte.

`RepositorioSubastasMongo.buscarPorAutor` lo intenta resolver con:

```java
Criteria.where("autor.$id").is(new ObjectId(perfilId))   // ← falla si perfilId no es 24-hex
```

`new ObjectId("1000")` lanza `IllegalArgumentException: invalid hexadecimal representation`. El seed (PR #104) crea perfiles con id `"1000"`, `"1001"`, etc. → este endpoint **explota para perfiles del seed**.

`RepositorioSubastasMongo.buscarDondeParticipa` usa:

```java
Criteria.where("usuario.id").is(userId)   // ← misma trampa que con DBRef
```

**Acción consolidada:**
1. Decidir si los `Perfil._id` son `String` arbitrarios o `ObjectId` autogenerados. **Adoptar una sola convención** y aplicarla al seed.
2. Reescribir todas las queries que tocan `@DBRef` para usar `autor.$id` (no `autor.id`) y wrappear con `new ObjectId(...)` solo si la decisión fue `ObjectId`.
3. Agregar test de integración para cada `buscarPor*Id` con el seed real.

### 3.6 🔴 `ServicioSubasta.crearSubasta` recibe `perfilId` pero llama `buscarPorUsuarioId`

`ControladorSubasta.crearSubasta`:

```java
String perfilId = this.obtenerPerfilIdDeCookie(token);
this.subastaService.crearSubasta(perfilId, ...);
```

Pero el servicio:

```java
public void crearSubasta(String userId, String figuritaId, ...) {
  Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);   // ← usa como userId
  ...
}
```

`buscarPorUsuarioId(perfilId)` no va a encontrar el perfil porque `perfilId` no coincide con `usuario.$id`. **Crear subasta queda roto post-merge**.

Comparar con `ofertarEnSubasta(String autorId, ...)` que sí usa `buscarPorId(autorId)` (consistente). El servicio fue migrado parcialmente.

**Acción:** uniformizar todos los servicios para recibir `perfilId` (no `userId`) y usar `buscarPorId`. Renombrar params para clarificar.

### 3.7 🔴 `ServicioSubasta.obtenerSubastasParticipo` y `obtenerOferta` mismo bug

```java
public SubastasParticipoResponseDto obtenerSubastasParticipo(String userId) {
  List<Subasta> subastas = this.repoSubasta.buscarDondeParticipa(userId);
  ...
  .map(s -> new SubastaParticipoDto(s, obtenerOferta(s, userId)))
}

private Propuesta obtenerOferta(Subasta subasta, String userId) {
  return subasta.getOfertas().stream()
      .filter(p -> p.getAutor().getUsuario().getId().equals(userId))   // ← compara usuario.id con perfilId
      .findFirst().get();
}
```

El controller pasa `perfilId`. El filtro compara con `usuario.getId()`. Nunca matchea → `Optional.get()` → `NoSuchElementException`.

### 3.8 🟠 `ServicioPerfil.agregarCalificacion` — naming PascalCase + ambigüedad

```java
public void agregarCalificacion(String AutorId, String DestinoId, ...) {
  Perfil perfilDestino = this.repositorioPerfiles.buscarPorId(DestinoId);
  Perfil autor = this.repositorioPerfiles.buscarPorId(AutorId);
  ...
}
```

1. Parámetros con PascalCase (no es Java).
2. Antes era `buscarPorUsuarioId(userAutorId)`, ahora es `buscarPorId(AutorId)`. La transición fue silenciosa.
3. No hay validación de que el "autor" sea distinto del "destino" (un usuario puede autocalificarse).

### 3.9 🟠 `ServicioPropuesta.buscarPropuestas` — NPE en filtros

```java
if (filtros.tipo().equals("RECIBIDAS")) {...}
else if (filtros.tipo().equals("ENVIADAS")) {...}
// Si filtros.tipo() == null → NPE
// Si filtros.tipo() == "OTRO" → devuelve PaginaResultado vacía pero (0,0,0)
```

Defensa nula. Además, comparar strings con `equals()` cuando hay un enum disponible (`EstadoProceso`) es feo. Convertir `tipo` a enum (`TipoPropuesta { RECIBIDAS, ENVIADAS }`) y usar switch.

### 3.10 🟠 `ServicioUsuario.registrar` — sin transacción ni idempotencia

```java
public void registrar(UsuarioRequest request) {
  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();   // ← se instancia cada vez
  Usuario usuarioNuevo = ...;
  this.repositorioUsuarios.guardar(usuarioNuevo);
  if (Rol.ADMINISTRADOR.equals(request.getRol())) return;
  Coleccion coleccion = new Coleccion();
  this.repositorioColecciones.guardar(coleccion);
  Perfil perfil = new Perfil(usuarioNuevo, ...);
  this.repositorioPerfiles.guardar(perfil);
}
```

Problemas:
1. **Sin transacción.** Si falla `repositorioPerfiles.guardar`, queda un `Usuario` huérfano + una `Coleccion` huérfana. El siguiente registro con el mismo `nombre` falla solo si hay índice único (que actualmente no existe). Sin índice único, se duplican.
2. **Sin verificación de unicidad de `nombre`.** Mongo no falla porque cada doc tiene `_id` distinto autogenerado. Dos usuarios pueden tener el mismo `nombre`, y `buscarPorNombre` con `findOne` devuelve el primero → login impredecible.
3. **`PasswordEncoder` instanciado en cada llamada.** No es performance crítico (BCrypt es 100ms+) pero es ruido y va contra el patrón de bean.

### 3.11 🟠 `ServicioPerfil.obtenerContadores` / `obtenerNotificaciones` siguen rotos

```java
public List<NotificacionesDto> obtenerNotificaciones(String userId) {
  Perfil perfil = repositorioPerfiles.buscarPorId(userId);   // ← param llamado userId pero es perfilId
  return this.repositorioNotificaciones.buscarPorPerfil(perfil)...
}
```

El parámetro se sigue llamando `userId` aunque ahora sí se trata como `perfilId`. Cobertura post-PR sin renombrar.

### 3.12 🟠 `ServicioPerfil.obtenerSugerencias` aún devuelve `(0, 0, 0)`

Bug heredado del PR #104 §3.7, no se arregló acá. Sigue habiendo un TODO.

### 3.13 🟠 `ControladorPerfil` — comentario de código muerto

Bloque comentado (sugerencias) ocupa ~10 líneas. Borrar.

### 3.14 🟠 Path-variable `oferta_id` no se valida que pertenezca a la subasta

`seleccionarOferta`, `rechazarOferta`, `mejorarOfertaEnSubasta` buscan la oferta dentro de `subasta.getOfertas()`. Si no la encuentran, lanzan `BadRequestException("Oferta no encontrada")`. Hasta acá OK.

Pero `mejorarOfertaEnSubasta` **no valida** que el caller sea el autor de la oferta. Cualquiera puede modificar las figuritas ofrecidas de otro usuario. Vulnerabilidad gemela a §2.6.

### 3.15 🟠 `seleccionarOferta` — bypass de validación de transición

```java
subasta.getOfertas().stream()
    .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.SELECCIONADO)
    .findFirst()
    .ifPresent(p -> p.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE)));
```

Mete una transición `SELECCIONADO → PENDIENTE` directamente, sin pasar por método de dominio. Otra propuesta puede quedar en estados inconsistentes.

### 3.16 🟠 `ErrorHandler.handleInternalServerError` esconde mucho

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
  ErrorResponse error = new ErrorResponse(500, "Ocurrió un error interno...", Map.of(), now);
  ...
}
```

Catch-all que devuelve 500 para **toda** excepción no manejada incluye:
- `HttpMessageNotReadableException` (body inválido) — debería ser 400.
- `MethodArgumentTypeMismatchException` (query param con tipo malo) — debería ser 400.
- `HttpRequestMethodNotSupportedException` — debería ser 405.

Resultado: el cliente recibe 500 para errores que son por su culpa.

Y críticamente: **no hay log del stacktrace**. La excepción se traga silenciosa, el cliente recibe genérico, y nadie sabe qué pasó.

### 3.17 🟠 Typo `handleUnathorized`

```java
public ResponseEntity<ErrorResponse> handleUnathorized(UnauthorizedException ex) {...}
```

Debe ser `handleUnauthorized`. El test también tiene el typo (`handleUnauthorized_devuelve401` invoca `errorHandler.handleUnathorized(ex)`).

---

## 4. Frontend

### 4.1 🟠 `userContext` con estado tri-valued

```javascript
const [user, setUser] = useState(() => {
  const storedUser = localStorage.getItem("sesion");
  return storedUser ? JSON.parse(storedUser) : undefined;
});

const tieneSesion = user !== undefined
```

Se mezclan `null`, `undefined` y objeto. En distintos lugares:
- `if (user == null) return;` (useEffect)
- `user !== undefined` (tieneSesion)
- `user ? <Outlet /> : <Navigate />` (RutaProtegida)

Tres convenciones. Resultado: hay edge cases donde `user === null` (no setear nunca) puede engañar al sistema. **Acción:** elegir un valor sentinel único (típicamente `null` para "no sesión", objeto para "sesión activa", y un loading flag separado para "todavía no lo sé").

### 4.2 🟠 Race condition en `RutaProtegida` al cargar localStorage

```javascript
const RutaProtegida = () => {
  const { user } = useAuth();
  return user
    ? <Outlet />
    : <Navigate to="/acceso-denegado" replace />;
}
```

Si el usuario hace refresh estando logueado, el `localStorage` tiene la sesión, pero el `useEffect` en `AuthProvider` llama a `verificarSesion()` (que pega `/yo`) **asincrónicamente**. Mientras tanto, `user` ya está seteado desde `localStorage`. Si el backend invalidó la sesión (cookie expirada), el `/yo` falla → el interceptor dispara `logout` → `user = undefined` → redirige.

Pero en el ínterin, `RutaProtegida` renderiza el componente protegido. Eso es OK, pero si ese componente hace su propio fetch protegido, va a recibir 401 y otro logout. No es bug crítico, pero hay flicker y races.

**Acción:** introducir un estado `loading` que `RutaProtegida` respete (`if (loading) return <Spinner />;`).

### 4.3 🟠 Import circular `api ↔ sesionService`

`api.js`:
```javascript
import { logout } from "@/services/sesionService.js";
```

`sesionService.js`:
```javascript
import { api } from "./api.js";
```

JavaScript suele manejar circulares con módulos ES, pero el orden de evaluación importa. En Vite/Webpack, el import de `logout` puede quedar `undefined` durante la inicialización. Si el interceptor se ejecuta antes de que `sesionService` termine de evaluar, `logout()` es `undefined` y tira `TypeError`. No es determinístico.

**Acción:** invertir la dependencia: en `api.js`, llamar al endpoint `/sesion` con `axios` directo (no via `sesionService`).

### 4.4 🟠 `login.jsx` mezcla controlado/no controlado

```javascript
const [formData, setFormData] = useState({nombre: "", contrasenia: ""});
...
await iniciarSesion({
  nombre: e.target.nombre.value,        // ← lee del DOM
  contrasenia: e.target.contrasenia.value
})
```

Pero el input tiene `value={formData.nombre}` (controlado) y `onChange={handleChange}` que actualiza `formData`. Cuando se envía el form, leer `e.target.nombre.value` es redundante con `formData.nombre` y rompe el principio de "single source of truth". Si se cambia el binding, esto se desincroniza.

### 4.5 🟠 `setErrorState("string")` sobre un template objeto

```javascript
const [errorState, setErrorState] = useState(errorTemplate({nombre:undefined, contrasenia: undefined}));
...
if (!formData.nombre || !formData.contrasenia) {
  setErrorState("Completa todos los campos");   // ← reemplaza objeto por string
  return;
}
```

El componente seguramente lee `errorState.nombre` en algún lado. Reemplazar el objeto por un string rompe ese acceso.

### 4.6 🟠 `Administrador` importado pero no rutado

```javascript
import Administrador from './views/public/administrador/administrador.jsx'
```

Pero `Administrador` no aparece ni en `publics` ni en `privates`. Import muerto (o ruta olvidada).

### 4.7 🟠 No hay protección por rol en el frontend

`RutaProtegida` permite cualquier usuario logueado. No filtra admin. Combinado con §2.5, si alguien hace `fetch("/administrador/estadisticas")` desde la consola siendo usuario normal, recibe los datos.

### 4.8 🟠 CORS hardcoded a `http://localhost:5173`

```java
registry.addMapping("/**")
    .allowedOrigins("http://localhost:5173")
    ...
```

No funciona en deploy. Mover a config externa (`@Value("${cors.allowed-origins}")`).

### 4.9 🟠 localStorage con datos de sesión

El JWT está en cookie HttpOnly (✅), pero el `user` (con `usuarioId`, `perfilId`, `rol`, `colId`) está en `localStorage`. Esto no permite robar el token, pero un XSS sí puede inferir privilegios y operar con la cookie automática del browser.

**Acción mínima:** no guardar `rol` en localStorage. Validar el rol siempre server-side (lo cual ya debería pasar; el `localStorage` es solo para UI).

---

## 5. Diseño y arquitectura

### 5.1 🟠 Token re-parseado en cada controller

```java
private String obtenerPerfilIdDeCookie(String token) {
  return this.servicioJwt.getPerfilId(token);   // ← parsea + valida el JWT
}
```

`getPerfilId`, `getColeccionId`, `getUsuarioId` cada uno llama `parseClaimsJws`. Por cada request:
1. `JwtFilter.validarToken` (1 parse).
2. `controller.obtenerPerfilIdDeCookie` (2do parse).
3. Si el controller también necesita `colId` u otro claim → 3er parse.

BCrypt-parse no es tan caro pero es desperdicio. **Acción:** que el filtro setee los claims en `request.setAttribute("claims", claims)` y los controllers los lean vía `@RequestAttribute` o un argument resolver custom.

Alternativa: integrar Spring Security con `JwtAuthenticationToken` y usar `@AuthenticationPrincipal`.

### 5.2 🟠 `ServicioJwt` duplica lógica

```java
public Claims validarToken(String token) {
  return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
}

private Claims obtenerAtributos(String token) {
  return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
}
```

Idénticos. Y `obtenerSesion` usa `validarToken`; `getColeccionId/getUsuarioId/getPerfilId` usan `obtenerAtributos`. Unificar.

### 5.3 🟠 `JwtFilter` reescribe CORS dentro del handler

```java
} catch (JwtException | UnauthorizedException e) {
  var corsConfig = corsConfigurationSource.getCorsConfiguration(request);
  if (corsConfig != null) {
    String origin = request.getHeader("Origin");
    if (corsConfig.checkOrigin(origin) != null) {
      response.setHeader("Access-Control-Allow-Origin", origin);
      response.setHeader("Access-Control-Allow-Credentials", "true");
    }
  }
  errorHandler.resolveException(request, response, null, new UnauthorizedException("Token inválido"));
}
```

Workaround porque Spring no aplica CORS automáticamente en respuestas servidas por filtros. Funciona pero es frágil — si se cambia `CorsConfig`, hay que recordar este path. Mejor: hacer que el filtro delegue la excepción y dejar que Spring (con un `WebSecurityConfig` o `OncePerRequestFilter` adicional) maneje CORS.

### 5.4 🟠 `ControladorSesion` sin `@RequestMapping` base

```java
@RestController
public class ControladorSesion {
  @GetMapping("/administrador/estadisticas") ...
  @PostMapping("/login") ...
  @GetMapping("/yo") ...
  @DeleteMapping("/sesion") ...
}
```

Cuatro endpoints distintos sin prefijo común y mezclando dominios (estadísticas + sesión). `/administrador/estadisticas` debería migrar a otro controller. `/yo` y `/sesion` deberían quedar agrupados, idealmente bajo `/sesion`.

### 5.5 🟠 `PropuestasDto`, `CalificacionesDto`, `PaginaResultado` duplicados

```java
public record PaginaResultado<T>(List<T> contenido, long cantidadDeElementos, int cantidadDePaginas, int numero) {...}
public class PropuestasDto { List<PropuestaDto> data; int resultados; int paginaActual; int paginasTotales; }
public class CalificacionesDto { List<CalificacionDto> data; int resultados; int paginaActual; int paginasTotales; }
```

Tres representaciones de "página de cosas". La que se usa es `PaginaResultado` (el resto está hardcoded sin uso). Eliminar `PropuestasDto` y `CalificacionesDto`.

### 5.6 🟠 `Logging` con TRACE en producción

(Ya cubierto en §2.10.)

### 5.7 🟠 Excepciones inconsistentes: `UsuarioException extends BadRequestException` → 400

Login fallido devuelve 400 en lugar de 401. La especificación HTTP dice 401 cuando "the request lacks valid authentication credentials". Cambiar `UsuarioException` para que herede de `UnauthorizedException` o crear `CredencialesInvalidasException extends UnauthorizedException`.

### 5.8 🟠 `Manejo-de-autenticacion.md` documenta el comportamiento, no advierte de los riesgos

El doc es bueno como referencia pero omite:
- Que el SECRET está hardcoded (probablemente porque la doc se escribió antes de evaluar la decisión).
- Que `Rol` viene del cliente.
- Que no hay autorización por rol.
- Que no hay invalidación server-side.

Sumar una sección "Limitaciones conocidas" o "Modelo de amenazas" para que el lector entienda los compromisos.

---

## 6. Tests

### Aspectos positivos

- `ServicioJwtTest` cubre el round-trip generar/validar.
- `FiltroJwtTest` cubre paths excluidos y autenticación válida/inválida.
- `ManejadorDeErroresTest` cubre los 4 handlers.
- `ServicioSesionTest`, `ServicioNotificacionTest`, `ControladorSesionTest` agregados.

### Faltantes / mejoras

- `Propuesta.cancelar` con el bug §3.1 — el test (si existe) probablemente está pasando porque valida con el destinatario, no con el autor.
- `ServicioSesion.login` con usuario inexistente: si tira `NotFoundException`, el test debería capturarlo. Si captura, el test refuerza el bug §2.7.
- `ServicioUsuario.registrar` con nombre duplicado: probable que no esté testado, ergo no detecta §3.10.
- Pruebas de integración para los queries Mongo (§3.5) — no parecen existir.
- Pruebas E2E que simulen "login → acción sensible que requiere ownership" para detectar §2.6.
- `handleUnathorized` típo no detectado por el test.

---

## 7. Plan de acción priorizado

### Crítico (bloqueante para producción)

1. **Mover el SECRET de JWT a variable de entorno** (§2.1). Rotar valor. Purgar del git history.
2. **Eliminar `Rol` de `UsuarioRequest`** (§2.2). Cualquier path de promoción a admin requiere endpoint protegido.
3. **Validar ownership en endpoints de `Subasta`** (cancelar, cerrar, seleccionar, rechazar, mejorar) (§2.6, §3.14).
4. **Restringir endpoints administrativos por rol** (§2.5).
5. **Corregir `Propuesta.validarUsuarioAutor`** (§3.1).
6. **Corregir queries Mongo con `@DBRef`** (§3.5). Acompañar de tests de integración.
7. **`ServicioSubasta.crearSubasta` debe usar `buscarPorId`, no `buscarPorUsuarioId`** (§3.6). Idem `obtenerSubastasParticipo` y `obtenerOferta` (§3.7).
8. **Login: convertir `NotFoundException` → `UnauthorizedException`** (§2.7, §3.2).

### Alto

9. **Reemplazar `path.startsWith` por igualdad exacta o lista whitelisted con `equals`** (§2.4).
10. **Decidir si `/figuritas` es público** y, si lo es, devolver DTO de catálogo sin datos de usuarios (§2.3).
11. **Cookie `secure` parametrizado por profile** (§2.8).
12. **Transacción en `ServicioUsuario.registrar`** + índice único en `Usuario.nombre` (§3.10).
13. **`Perfil.getIniciales()` resistente a nombres cortos / null** (§3.3) y `CalificacionDto` resistente a autor null (§3.4).
14. **Refactor: claims del JWT setear como `request.attribute` en el filtro** y consumir en controllers vía argument resolver (§5.1).
15. **`ErrorHandler` con handlers específicos** para `MethodArgumentNotValid`, `HttpMessageNotReadable`, etc., y logging del stacktrace en el catch-all (§3.16).
16. **`UsuarioException` debe heredar de `UnauthorizedException`** para devolver 401 (§5.7).

### Medio

17. **Bypass de domain methods** en `ServicioSubasta` (§3.15) y eliminación de `seleccionarOferta` que modifica estado directamente.
18. **Mejoras frontend**: import circular (§4.3), tri-valued state (§4.1), race en `RutaProtegida` (§4.2), login form (§4.4, §4.5).
19. **Limpiar imports muertos (`Administrador`)** y comentarios `//` muertos (§3.13, §4.6).
20. **Sumar autorización por rol en frontend (`RutaProtegidaPorRol`)** (§4.7).
21. **CORS por config** (§4.8).
22. **Eliminar duplicación** `PropuestasDto`/`CalificacionesDto` vs `PaginaResultado` (§5.5).
23. **`ServicioJwt`** unificar `validarToken` y `obtenerAtributos` (§5.2).
24. **Validar enum (no string) en `ServicioPropuesta.buscarPropuestas`** (§3.9).
25. **Renombrar parámetros confusos** (`AutorId` → `autorId`, `userId` → `perfilId` donde corresponda) (§3.8, §3.11).

### Bajo

26. **Logging level** (§2.10).
27. **Rate limiting en `/login`** (§2.11).
28. **Refresh tokens / TTL corto + blacklist** (§2.9).
29. **Typo `handleUnathorized`** (§3.17).
30. **Doc `Manejo-de-autenticacion.md` sumar "Limitaciones conocidas"** (§5.8).
31. **Tests faltantes** (§6 — Faltantes).

---

## 8. Modelo de amenazas resumido

| Amenaza | Vector | Estado actual |
|---|---|---|
| Forjar tokens | SECRET hardcoded en repo público | **VULNERABLE** |
| Privilege escalation | `Rol` desde el body | **VULNERABLE** |
| IDOR (modificar/cancelar subastas ajenas) | Endpoints sin validación de ownership | **VULNERABLE** |
| Listar admin endpoints sin ser admin | No hay role-based authz | **VULNERABLE** |
| Robar token via JS | Cookie HttpOnly | Protegido ✅ |
| Robar token vía MITM | Cookie Secure | Protegido en prod ✅ (no en dev) |
| Username enumeration | Errores distintos según existe / no existe | **VULNERABLE** |
| Brute force de contraseñas | Sin rate limiting | Mitigado parcial por BCrypt |
| CSRF | Cookie SameSite=None | **PARCIALMENTE EXPUESTO** (no hay CSRF tokens) |
| Session hijack persistente post-logout | Sin invalidación server-side de JWT | Limitado a TTL (12h) |
| Cookie XSS | HttpOnly + Secure | Protegido ✅ |

---

## 9. Apéndice: archivos clave

| Archivo | Líneas | Comentario |
|---|---|---|
| `ServicioJwt.java` | 113 | Bug del SECRET. Lógica simple, fácil de fixear. |
| `JwtFilter.java` | 121 | CORS-in-filter es frágil. Filtros publicos con `startsWith`. |
| `ErrorHandler.java` | 68 | Mejorado respecto a #104. Le falta más granularidad y logging. |
| `ControladorSesion.java` | 73 | Falta cohesión, mezcla estadísticas y sesión. |
| `userContext.jsx` | 87 | Tri-valued state, race condition en `verificarSesion`. |
| `api.js` | 47 | Interceptor + import circular potencial. |
| `Manejo-de-autenticacion.md` | 763 | Buena doc, falta sección de limitaciones. |
| `Manejo-de-errores.md` | 574 | Buena doc, refleja lo implementado. |
| `application.properties` | 4 | **MongoDB URI con secret + log TRACE**. |
