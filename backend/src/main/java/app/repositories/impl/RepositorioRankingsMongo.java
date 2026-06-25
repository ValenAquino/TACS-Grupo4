package app.repositories.impl;

import app.dto.RankingUsuarioDto;
import app.repositories.RepositorioRankings;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioRankingsMongo implements RepositorioRankings {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<RankingUsuarioDto> topCreadoresDePropuestas(LocalDateTime desde, LocalDateTime hasta, int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.match(Criteria.where("estado.0.fecha").gte(desde).lte(hasta)));
    ops.add(context -> new Document("$group", new Document("_id", "$autor.$id")
        .append("valor", new Document("$sum", 1))));
    ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "valor")));
    ops.add(Aggregation.limit(limite));
    ops.addAll(lookupPerfilYProyectar(null));

    return ejecutar(ops, "propuestas");
  }

  @Override
  public List<RankingUsuarioDto> topIntercambiadores(LocalDateTime desde, LocalDateTime hasta, int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    // Filtra por fecha de creación y por último estado == ACEPTADO
    // (estadoActual no es fiable: Builder lo deja en PENDIENTE si no se setea explícitamente)
    ops.add(Aggregation.match(Criteria.where("estado.0.fecha").gte(desde).lte(hasta)));
    ops.add(context -> new Document("$match", new Document("$expr",
        new Document("$eq", List.of(
            new Document("$arrayElemAt", List.of("$estado.valor", -1)),
            "ACEPTADO"
        )))));
    ops.add(context -> new Document("$addFields", new Document("participantes",
        new Document("$concatArrays", List.of(
            List.of("$autor.$id"),
            List.of("$destinatario.$id")
        )))));
    ops.add(Aggregation.unwind("participantes"));
    ops.add(context -> new Document("$group", new Document("_id", "$participantes")
        .append("valor", new Document("$sum", 1))));
    ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "valor")));
    ops.add(Aggregation.limit(limite));
    ops.addAll(lookupPerfilYProyectar(null));

    return ejecutar(ops, "propuestas");
  }

  @Override
  public List<RankingUsuarioDto> mejorTasaAceptacion(LocalDateTime desde, LocalDateTime hasta, int minimo, int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.match(Criteria.where("estado.0.fecha").gte(desde).lte(hasta)));
    ops.add(context -> new Document("$group", new Document("_id", "$destinatario.$id")
        .append("total", new Document("$sum", 1))
        .append("aceptadas", new Document("$sum", new Document("$cond", List.of(
            new Document("$eq", List.of(
                new Document("$arrayElemAt", List.of("$estado.valor", -1)),
                "ACEPTADO"
            )), 1, 0))))));
    ops.add(Aggregation.match(Criteria.where("total").gte(minimo)));
    ops.add(context -> new Document("$addFields", new Document("valor", new Document("$multiply", List.of(
        new Document("$divide", List.of("$aceptadas", "$total")), 100)))));
    ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "valor")));
    ops.add(Aggregation.limit(limite));
    ops.add(Aggregation.lookup("perfiles", "_id", "_id", "perfil"));
    ops.add(Aggregation.unwind("perfil"));
    ops.add(context -> new Document("$project", new Document("_id", 0)
        .append("perfilId", new Document("$toString", "$_id"))
        .append("nombre", "$perfil.nombre")
        .append("valor", "$valor")
        .append("detalle", new Document("$concat", List.of(
            new Document("$toString", "$aceptadas"), "/", new Document("$toString", "$total"))))));

    return ejecutar(ops, "propuestas");
  }

  @Override
  public List<RankingUsuarioDto> topSubastadores(LocalDateTime desde, LocalDateTime hasta, int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.match(Criteria.where("fechaInicio").gte(desde).lte(hasta)));
    ops.add(context -> new Document("$group", new Document("_id", "$autor.$id")
        .append("valor", new Document("$sum", 1))));
    ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "valor")));
    ops.add(Aggregation.limit(limite));
    ops.addAll(lookupPerfilYProyectar(null));

    return ejecutar(ops, "subastas");
  }

  @Override
  public List<RankingUsuarioDto> mejorReputacion(int minimoCalificaciones, int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.match(Criteria.where("cantidadCalificaciones").gte(minimoCalificaciones)));
    ops.add(Aggregation.sort(Sort.by(
        Sort.Order.desc("calificacionMedia"),
        Sort.Order.desc("cantidadCalificaciones"))));
    ops.add(Aggregation.limit(limite));
    ops.add(context -> new Document("$project", new Document("_id", 0)
        .append("perfilId", new Document("$toString", "$_id"))
        .append("nombre", "$nombre")
        .append("valor", "$calificacionMedia")
        .append("detalle", new Document("$concat", List.of(
            new Document("$toString", "$cantidadCalificaciones"), " calificaciones")))));

    return ejecutar(ops, "perfiles");
  }

  @Override
  public List<RankingUsuarioDto> topColeccionistas(int limite) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.lookup("colecciones", "coleccion.$id", "_id", "col"));
    ops.add(Aggregation.unwind("col"));
    ops.add(context -> new Document("$addFields", new Document("valor",
        new Document("$sum", "$col.repetidas.cantidadExistente"))));
    ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "valor")));
    ops.add(Aggregation.limit(limite));
    ops.add(context -> new Document("$project", new Document("_id", 0)
        .append("perfilId", new Document("$toString", "$_id"))
        .append("nombre", "$nombre")
        .append("valor", "$valor")));

    return ejecutar(ops, "perfiles");
  }

  /**
   * Etapas finales comunes para los rankings que agrupan por el id del perfil:
   * resuelve el nombre del perfil con un {@code $lookup} y proyecta el resultado
   * al formato de {@link RankingUsuarioDto}.
   *
   * @param detalle expresión opcional para el campo detalle (puede ser {@code null})
   */
  private List<AggregationOperation> lookupPerfilYProyectar(Object detalle) {
    List<AggregationOperation> ops = new ArrayList<>();
    ops.add(Aggregation.lookup("perfiles", "_id", "_id", "perfil"));
    ops.add(Aggregation.unwind("perfil"));
    ops.add(context -> {
      Document project = new Document("_id", 0)
          .append("perfilId", new Document("$toString", "$_id"))
          .append("nombre", "$perfil.nombre")
          .append("valor", "$valor");
      if (detalle != null) {
        project.append("detalle", detalle);
      }
      return new Document("$project", project);
    });
    return ops;
  }

  /**
   * Ejecuta el pipeline de agregación sobre la colección indicada y mapea
   * los documentos resultantes a {@link RankingUsuarioDto}.
   */
  private List<RankingUsuarioDto> ejecutar(List<AggregationOperation> ops, String coleccion) {
    AggregationResults<Document> resultado =
        mongoTemplate.aggregate(Aggregation.newAggregation(ops), coleccion, Document.class);

    return resultado.getMappedResults().stream().map(this::mapear).toList();
  }

  private RankingUsuarioDto mapear(Document doc) {
    Object perfilId = doc.get("perfilId");
    Number valor = doc.get("valor", Number.class);

    return new RankingUsuarioDto(
        perfilId != null ? perfilId.toString() : null,
        doc.getString("nombre"),
        valor != null ? valor.doubleValue() : 0.0,
        doc.getString("detalle")
    );
  }
}
