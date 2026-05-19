package app.repositories.implMongo;

import app.dto.filtros.SugerenciasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Sugerencia;
import app.repositories.RepositorioPerfiles;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class RepositorioPerfilesMongo implements RepositorioPerfiles {

  @Autowired
  private MongoTemplate mongoTemplate;

  public Perfil buscarPorId(String id) {
    Perfil perfil = mongoTemplate.findById(id, Perfil.class);

    if( perfil == null) {
      throw new NotFoundException("Perfil no encontrado con id: " + id);
    }
    return perfil;
  }

  public Perfil buscarPorUsuarioId(String usuarioId) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("usuario.id").is(usuarioId)
    );

    Perfil perfil = mongoTemplate.findOne(query, Perfil.class);
    if( perfil == null) {
      throw new NotFoundException("Perfil no encontrado con usuario de id: " + usuarioId);
    }

    return perfil;
  }

  public List<Perfil> buscarPorFiguritaFaltante(Figurita figurita) {
    Query queryColecciones = new Query(
        Criteria.where("faltantes.$id").is(figurita.getId())
    );
    List<Coleccion> colecciones = mongoTemplate.find(queryColecciones, Coleccion.class);

    List<String> idsColecciones = colecciones.stream()
        .map(Coleccion::getId)
        .toList();

    Query queryPerfiles = new Query(
        Criteria.where("coleccion.$id").in(idsColecciones)
    );
    return mongoTemplate.find(queryPerfiles, Perfil.class);
  }

  public List<Perfil> buscarTodos() {
    return mongoTemplate.findAll(Perfil.class);
  }

  public long contar() {
    Query query = new Query();
    return mongoTemplate.count(query, Perfil.class);
  }

  public void guardar(Perfil perfil) {
    mongoTemplate.save(perfil);
  }

  public PaginaResultado<Sugerencia> generarSugerencias(Coleccion coleccionObjetivo, SugerenciasFiltro filtro) {

    List<String> faltantesObjetivo = coleccionObjetivo.getFaltantes()
        .stream().map(Figurita::getId).toList();

    List<String> repetidasObjetivo = coleccionObjetivo.getRepetidas().stream()
        .map(r -> r.getFigurita().getId())
        .toList();

    // Ops base (sin paginación)
    List<AggregationOperation> ops = new ArrayList<>();

    ops.add(Aggregation.match(
        Criteria.where("_id").ne(coleccionObjetivo.getId())
    ));

    ops.add(Aggregation.lookup("perfiles", "_id", "coleccion.$id", "perfil"));
    ops.add(Aggregation.unwind("perfil"));

    ops.add(context -> new Document("$addFields", new Document()
        .append("sugeridas", new Document("$filter", new Document()
            .append("input", "$repetidas")
            .append("as", "r")
            .append("cond", new Document("$in", List.of(
                new Document("$toString", "$$r.figurita.$id"),
                faltantesObjetivo
            )))
        ))
        .append("necesarias", new Document("$filter", new Document()
            .append("input", "$faltantes")
            .append("as", "f")
            .append("cond", new Document("$in", List.of(
                new Document("$toString", "$$f.$id"),
                repetidasObjetivo
            )))
        ))
    ));

    ops.add(Aggregation.match(
        new Criteria().andOperator(
            Criteria.where("sugeridas.0").exists(true),
            Criteria.where("necesarias.0").exists(true)
        )
    ));

    if (Objects.equals(filtro.tipo(), "1a1")) {
      ops.add(Aggregation.match(
          Criteria.where("sugeridas").size(1).and("necesarias").size(1)
      ));
    } else if (Objects.equals(filtro.tipo(), "Na1")) {
      ops.add(context -> new Document("$match", new Document("$expr", new Document("$and", List.of(
          new Document("$gt", List.of(new Document("$size", "$sugeridas"), 1)),
          new Document("$eq", List.of(new Document("$size", "$necesarias"), 1))
      )))));
    } else if (Objects.equals(filtro.tipo(), "1aN")) {
      ops.add(context -> new Document("$match", new Document("$expr", new Document("$and", List.of(
          new Document("$eq", List.of(new Document("$size", "$sugeridas"), 1)),
          new Document("$gt", List.of(new Document("$size", "$necesarias"), 1))
      )))));
    }

    // Count: mismas ops + $count
    List<AggregationOperation> countOps = new ArrayList<>(ops);
    countOps.add(Aggregation.count().as("total"));
    Document countDoc = mongoTemplate.aggregate(
        Aggregation.newAggregation(countOps), "colecciones", Document.class
    ).getUniqueMappedResult();
    int total = countDoc != null ? countDoc.getInteger("total") : 0;

    // Paginación
    List<AggregationOperation> pageOps = new ArrayList<>(ops);
    pageOps.add(Aggregation.skip((long) (filtro.paginaActual() - 1) * filtro.limite()));
    pageOps.add(Aggregation.limit(filtro.limite()));

    AggregationResults<Document> resultados = mongoTemplate.aggregate(
        Aggregation.newAggregation(pageOps), "colecciones", Document.class
    );

    // Mapeo
    MongoConverter converter = mongoTemplate.getConverter();
    List<Sugerencia> sugerencias = resultados.getMappedResults().stream()
        .map(doc -> {
          Perfil perfil = converter.read(Perfil.class, (Document) doc.get("perfil"));
          List<Figurita> sugeridas = ((List<Document>) doc.get("sugeridas")).stream()
              .map(d -> converter.read(Figurita.class, d))
              .toList();
          List<Figurita> necesarias = ((List<Object>) doc.get("necesarias")).stream()
              .map(item -> {
                if (item instanceof com.mongodb.DBRef ref) {
                  return mongoTemplate.findById(ref.getId().toString(), Figurita.class);
                }
                return converter.read(Figurita.class, (Document) item);
              })
              .filter(Objects::nonNull)
              .toList();
          return new Sugerencia(perfil, sugeridas, necesarias);
        })
        .toList();

    return new PaginaResultado<>(
        sugerencias,
        total,
        (int) Math.ceil((double) total / filtro.limite()),
        filtro.paginaActual()
    );
  }
}

