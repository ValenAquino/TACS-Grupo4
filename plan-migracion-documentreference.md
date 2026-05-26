# Plan de Migración: `@DBRef` → `@DocumentReference`

## Contexto

El branch `feature/poc-mongodb` ya validó que `@DocumentReference` permite hacer queries con strings
directamente, sin conversiones manuales a `ObjectId` ni acceso a `.$id`. Este plan migra la rama
actual (`feature/mejorar-pantalla-explorar`) a ese patrón.

**Validación del POC:**
```java
// Con @DBRef (actual)              | Con @DocumentReference (objetivo)
Criteria.where("destinatario.$id")  | Criteria.where("destinatario")
  .is(new ObjectId(id))             |   .is(id)   // id como String, sin conversión
```

---

## Inventario de cambios

### Entidades (7 archivos — cambio mecánico)

| Archivo | Campos a migrar |
|---|---|
| `Calificacion.java` | `autor`, `destinatario` |
| `Subasta.java` | `autor`, `figuritaSubastada`, `figuritasSolicitadas` |
| `Propuesta.java` | `autor`, `destinatario`, `figuritasOfrecidas`, `figuritaBuscada` |
| `Perfil.java` | `usuario`, `coleccion` |
| `Coleccion.java` | `faltantes` |
| `FiguritaIntercambiable.java` | `figurita` |
| `Notificacion.java` | `perfil` |

En todos los casos: reemplazar `@DBRef` por `@DocumentReference`, actualizar imports
(`org.springframework.data.mongodb.core.mapping.DBRef` →
`org.springframework.data.mongodb.core.mapping.DocumentReference`).
El atributo `lazy = true` se mantiene igual en `@DocumentReference(lazy = true)`.

---

### Repositorios (3 archivos — cambios funcionales)

#### `RepositorioCalificacionesMongo.java`

**Eliminar completamente:**
- El helper privado `toId()` (líneas 24–26)
- El import `org.bson.types.ObjectId`

**Simplificar queries:**

```java
// buscarPorDestinatario — línea 37
// ANTES:
Criteria.where("destinatario.$id").is(toId(destinatarioId))
// DESPUÉS:
Criteria.where("destinatario").is(destinatarioId)

// yaCalifico — líneas 66 y 70
// ANTES:
Criteria.where("destinatario.$id").is(toId(perfilDestinoId))
Criteria.where("autor.$id").is(toId(perfilAutorId))
// DESPUÉS:
Criteria.where("destinatario").is(perfilDestinoId)
Criteria.where("autor").is(perfilAutorId)
```

---

#### `RepositorioSubastasMongo.java`

**Eliminar:**
- Import duplicado `org.bson.types.ObjectId` (aparece dos veces: líneas 4 y 16)
- Import `com.mongodb.DBRef`

**Cambio en `buscarTodos` — líneas 119–126:**

```java
// ANTES:
DBRef autorRef = new DBRef("perfiles", filtros.participanteId());
query.addCriteria(
    Criteria.where("ofertas").elemMatch(
        Criteria.where("autor").is(autorRef)
            .and("estadoActual.valor").ne(EstadoProceso.CANCELADO)
    )
);

// DESPUÉS:
query.addCriteria(
    Criteria.where("ofertas").elemMatch(
        Criteria.where("autor").is(filtros.participanteId())
            .and("estadoActual.valor").ne(EstadoProceso.CANCELADO)
    )
);
```

**Fix bug en `buscarPorAutor` — línea 70:**

Con `@DBRef`, la query `Criteria.where("autor").is(perfilId)` no matchea porque MongoDB almacena
el campo como `{ $ref, $id }`, no como string. Con `@DocumentReference` almacena directamente
el ID, entonces la misma query funciona sin cambios de código. El bug desaparece solo.

**Fix bug en `buscarDondeParticipa` — líneas 169–173:**

La query actual es incorrecta: busca `$id` en el array de `ofertas` (que son `Propuesta` embebidas),
pero debería buscar por el autor de cada oferta. Reescribir con aggregation, igual al POC:

```java
// ANTES (query incorrecta):
Query querySubastas = new Query(
    Criteria.where("ofertas").elemMatch(
        Criteria.where("$id").is(perfil.getId())
    )
);

// DESPUÉS (aggregation como en poc-mongodb):
Aggregation agregacion = Aggregation.newAggregation(
    Aggregation.lookup("propuestas", "ofertas", "_id", "ofertasData"),
    Aggregation.unwind("ofertasData"),
    Aggregation.match(Criteria.where("ofertasData.autor").is(perfil.getId())),
    Aggregation.group("_id")
);
return mongoTemplate
    .aggregate(agregacion, Subasta.class, Subasta.class)
    .getMappedResults();
```

---

#### `RepositorioColeccionesMongo.java`

Este es el archivo con más cambios. Todos los `.$id` aparecen en pipelines de agregación.

**Eliminar:** import `com.mongodb.DBRef`

**`buscarFaltantes` — línea 153:**
```java
// ANTES:
Aggregation.lookup("figuritas", "faltantes.$id", "_id", "faltantes")
// DESPUÉS:
Aggregation.lookup("figuritas", "faltantes", "_id", "faltantes")
```

**`buscarIntercambiablesConFiltros` — línea 194:**
```java
// ANTES:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita.$id", "_id", "repetidas.figurita"));
// DESPUÉS:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita", "_id", "repetidas.figurita"));
```

**`buscarIntercambiablesPorQuery` — línea 247:**
```java
// ANTES:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita.$id", "_id", "repetidas.figurita"));
// DESPUÉS:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita", "_id", "repetidas.figurita"));
```

**`buscarIntercambiablesPorPerfilId` — líneas 293–295:**

Con `@DocumentReference`, el campo `coleccion` en el documento raw ya no es un `DBRef` sino
directamente un `ObjectId`. El cast explota en runtime.

```java
// ANTES:
DBRef ref = (DBRef) doc.get("coleccion");
String colId = ref.getId().toString();

// DESPUÉS:
String colId = doc.get("coleccion").toString();
```

**`buscarIntercambiablesPorPerfilId` — línea 273:**
```java
// ANTES:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita.$id", "_id", "repetidas.figurita"));
// DESPUÉS:
ops.add(Aggregation.lookup("figuritas", "repetidas.figurita", "_id", "repetidas.figurita"));
```

---

#### `RepositorioPerfilesMongo.java`

**`generarSugerencias` — lookup de perfiles (línea 138):**
```java
// ANTES:
ops.add(Aggregation.lookup("perfiles", "_id", "coleccion.$id", "perfil"));
// DESPUÉS:
ops.add(Aggregation.lookup("perfiles", "_id", "coleccion", "perfil"));
```

**`generarSugerencias` — stage `$addFields` con expresiones sobre referencias:**

Con `@DocumentReference`, `figurita` en `FiguritaIntercambiable` y los elementos de `faltantes`
son ObjectIds directos, no documentos `{ $ref, $id }`.

```java
// ANTES — sugeridas: accede a $$r.figurita.$id
new Document("$toString", "$$r.figurita.$id")

// DESPUÉS — sugeridas: figurita ya ES el id directamente
new Document("$toString", "$$r.figurita")
```

```java
// ANTES — necesarias: accede a $$f.$id (faltantes son DBRef)
new Document("$toString", "$$f.$id")

// DESPUÉS — necesarias: el elemento ya ES el id directamente
new Document("$toString", "$$f")
```

**`generarSugerencias` — mapeo de `necesarias` (líneas 208–213):**

Con `@DBRef`, el filtro deja los elementos de `faltantes` como objetos `DBRef` en el resultado.
Con `@DocumentReference`, quedan como `ObjectId`. El `instanceof DBRef` deja de hacer match
y todos los elementos intentan ser leídos como `Document`, lo que falla.

```java
// ANTES:
.map(item -> {
    if (item instanceof com.mongodb.DBRef ref) {
        return mongoTemplate.findById(ref.getId().toString(), Figurita.class);
    }
    return converter.read(Figurita.class, (Document) item);
})

// DESPUÉS: los elementos son ObjectId, resolver directamente
.map(item -> mongoTemplate.findById(item.toString(), Figurita.class))
```

---

## Orden de ejecución

```
Fase 1 — Entidades (sin dependencias entre sí, se pueden hacer en paralelo)
  ├── Calificacion.java
  ├── Subasta.java
  ├── Propuesta.java
  ├── Perfil.java
  ├── Coleccion.java
  ├── FiguritaIntercambiable.java
  └── Notificacion.java

Fase 2 — Repositorios simples (después de Fase 1)
  ├── RepositorioCalificacionesMongo.java
  └── RepositorioSubastasMongo.java

Fase 3 — Repositorios con aggregation (después de Fase 1)
  ├── RepositorioColeccionesMongo.java
  └── RepositorioPerfilesMongo.java
```

Las fases 2 y 3 son independientes entre sí, se pueden hacer en paralelo.

---

## Consideraciones

### Datos existentes en MongoDB

`@DBRef` almacena `{ "$ref": "coleccion", "$id": ObjectId("...") }`.
`@DocumentReference` almacena directamente `ObjectId("...")`.

Los formatos son incompatibles: si hay datos en la BD al momento de migrar, Spring
no va a poder leerlos con la nueva anotación. **No es un problema** porque el proyecto usa
`InicializadorDeDatos` que recrea todos los datos al iniciar. Alcanza con reiniciar la app.

### Tests

Revisar si algún test construye fixtures con la estructura interna de `DBRef` (ej: documentos
con `{ "$ref": ..., "$id": ... }`). Si existe, hay que actualizar esos fixtures para que usen
IDs directos.

### `InicializadorDeDatos.java`

Usa `new ObjectId().toHexString()` para generar IDs únicos para el seed. Eso no cambia —
sigue siendo válido porque es generación de IDs, no consulta.