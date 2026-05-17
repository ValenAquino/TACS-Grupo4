package app.repositories.implMongo;

import app.dto.FaltantesDto;
import app.dto.PaginaResultado;
import app.dto.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioColeccionMongo implements RepositorioColecciones {
  @Autowired
  private MongoTemplate mongoTemplate;

  public Coleccion buscarPorId(String colId){
    return mongoTemplate.findById(colId, Coleccion.class);
  }

  public void guardar(Coleccion coleccion) {
    mongoTemplate.save(coleccion);
  }

  public Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros) {

    int pagina = filtros.pagina();
    int limite = filtros.limite();

    // ── Count ──────────────────────────────────────────────────────────────
    Aggregation countAggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("id").is(colId)),
        Aggregation.unwind("repetidas"),
        Aggregation.match(
            Criteria.where("repetidas.metodos").is(filtros.metodoIntercambio())
        ),
        Aggregation.count().as("total")
    );

    AggregationResults<Document> countResult =
        mongoTemplate.aggregate(countAggregation, "colecciones", Document.class);

    Document countDoc = countResult.getUniqueMappedResult();
    int total = countDoc != null ? countDoc.getInteger("total") : 0;

    // ── Datos paginados ────────────────────────────────────────────────────
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("id").is(colId)),
        Aggregation.unwind("repetidas"),
        Aggregation.match(
            Criteria.where("repetidas.metodos").is(filtros.metodoIntercambio())
        ),
        Aggregation.skip((long) (pagina - 1) * limite),
        Aggregation.limit(limite),
        Aggregation.replaceRoot("repetidas")
    );

    AggregationResults<Document> resultado =
        mongoTemplate.aggregate(aggregation, "colecciones", Document.class);

    MongoConverter converter = mongoTemplate.getConverter();

    List<FiguritaIntercambiable> figuritas = resultado.getMappedResults()
        .stream()
        .map(doc -> converter.read(FiguritaIntercambiable.class, doc))
        .toList();

    PaginaResultado<FiguritaIntercambiable> data = new PaginaResultado<>(figuritas, total, pagina, limite);

    return new Repetidas<>(0, 0, data);
  }

  public FaltantesDto buscarFaltantes(String colId, FaltantesFiltro filtros){
    return null;
  }
}
