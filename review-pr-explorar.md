# Review — Feature: Mejorar pantalla explorar (Issue #92)

**Rama:** `feature/mejorar-pantalla-explorar`  
**Fecha:** 2026-05-24  

---

## Resumen

El PR resuelve correctamente todos los objetivos del issue: elimina el mapeo innecesario en el service, expone `perfilId` en el DTO, corrige la key duplicada en React, renombra el endpoint y añade multi-selección de tipos. La serialización de arrays en Axios está correctamente configurada con `paramsSerializer: { indexes: null }`. No hay bugs bloqueantes — solo observaciones de diseño menores.

---

## Observaciones de diseño

### 🔵 Semántica `.all()` vs `.in()` para múltiple selección de tipos

**Archivo:** `RepositorioColeccionesMongo.java`

```java
Criteria.where("repetidas.metodos").all(filtros.tipos())
```

`.all()` exige que `metodos` contenga **todos** los tipos del filtro (AND). Si el usuario activa los dos chips, solo verá figuritas que tengan **ambos** métodos. En los datos actuales ninguna figurita tiene `[INTERCAMBIO, SUBASTA]` simultáneamente, así que activar ambos chips devuelve 0 resultados.

Si la intención es "mostrar figuritas que soporten al menos uno de los tipos seleccionados" (OR), la query sería:

```java
Criteria.where("repetidas.metodos").in(filtros.tipos())
```

Vale la pena decidir explícitamente la semántica antes de mergear.

---

### 🔵 `strCutout` nunca llega del backend

**Archivos:** `explorar-resultados.jsx:35`, `figurita-card.jsx`

`FiguritaIntercambiableDto` no tiene campo `strCutout`. El frontend accede `fig.str_cutout` que siempre es `undefined`, por lo que siempre se muestra el placeholder. La feature de imagen de jugador está inactiva hasta que el backend agregue ese campo.

---

### 🔵 Prop `figurita` (raw DTO) acoplada en `FiguritaCard`

**Archivo:** `figurita-card.jsx` → `CardBody`

`FiguritaCard` recibe el objeto completo del DTO solo para pasarlo como `state` al navegar. Acopla el componente de UI al contrato del DTO. Si el DTO cambia, hay que rastrear hasta la card. Alternativa: pasar solo `perfilId` y `figuritaId` desde `explorar-resultados` y armar el `state` ahí.

---

## Lo que está bien

| Área | Detalle |
|---|---|
| Endpoint renombrado | `/figuritas/intercambiables` refleja correctamente el recurso |
| `perfilId` expuesto en el DTO | El flujo "proponer intercambio" recibe el id correcto |
| Key React corregida | `${figurita_id}-${perfil_id}` es única |
| `mapFigurita` eliminado | Datos consumidos directamente del DTO, sin transformaciones |
| `resolverTipo` en utils | Función pura, bien ubicada |
| Tests del controlador | Paths y mocks actualizados correctamente |
| `FiguritaCard` dividida en `CardHero`/`CardBody` | Mejor legibilidad |
| Doble `<hr>` corregido | Los separadores están dentro del condicional de `nombreUsuario` |
| `enviando` en `useCrearPropuesta` | Previene doble submit |
| Typo corregido | "las figuritas" (plural) |
| `usePaginacion` desde 1 | Correcto para el backend 1-indexed (`skip((pagina-1)*limite)`) |
| JWT filter | `startsWith("/figuritas")` sigue cubriendo el nuevo endpoint |
| `handleError` en dependency array | Corrección de regla de hooks de React |
| Multi-selección de tipos | Chips toggle con array, UI correcta |

---

---

## Listo para mergear

No hay bloqueantes. Las observaciones de diseño son mejoras opcionales, no requisitos.
