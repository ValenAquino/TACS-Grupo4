package app.repositories.implMongo;

import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.FiguritasFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
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

@Repository
public class RepositorioColeccionesMongo implements RepositorioColecciones {
  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Coleccion coleccion) {
    mongoTemplate.save(coleccion);
  }

  public Coleccion buscarPorId(String colId){
    Coleccion coleccion = mongoTemplate.findById(colId, Coleccion.class);

    if(coleccion == null) {
      throw new NotFoundException("No se encontro la coleccion");
    }

    return coleccion;
  }

  public Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros) {

    int pagina = filtros.pagina();
    int limite = filtros.limite();

    // ── Count ──────────────────────────────────────────────────────────────

    int cantidadResultados = this.contarCampoEnColeccion(colId, "repetidas", List.of(Aggregation.match(
        Criteria.where("repetidas.metodos").is(filtros.metodoIntercambio())))
    );

    // ── Datos paginados ────────────────────────────────────────────────────


    AggregationResults<Document> resultado = this.buscarCampoEnColeccion(
        colId,
        "repetidas",
        List.of(Aggregation.match(
          Criteria.where("repetidas.metodos").is(filtros.metodoIntercambio())
      )),
        pagina,
        limite
    );

    MongoConverter converter = mongoTemplate.getConverter();

    List<FiguritaIntercambiable> figuritas = resultado.getMappedResults()
        .stream()
        .map(doc -> converter.read(FiguritaIntercambiable.class, doc))
        .toList();

    PaginaResultado<FiguritaIntercambiable> data = new PaginaResultado<>(figuritas, cantidadResultados, (int) Math.ceil( (double) cantidadResultados /limite), limite);

    return new Repetidas<>(0, 0, data);
  }

  public PaginaResultado<Figurita> buscarFaltantes(String colId, FaltantesFiltro filtros){
    int pagina = filtros.pagina();
    int limite = filtros.limite();

    // ── Count ──────────────────────────────────────────────────────────────

    int cantidadResultados = this.contarCampoEnColeccion(colId, "faltantes", new ArrayList<>());

    // ── Datos paginados ────────────────────────────────────────────────────


    AggregationResults<Document> resultado = this.buscarCampoEnColeccion(
        colId, "faltantes",
        new ArrayList<>(), pagina, limite);

    MongoConverter converter = mongoTemplate.getConverter();

    List<Figurita> figuritas = resultado.getMappedResults()
        .stream()
        .map(doc -> converter.read(Figurita.class, doc))
        .toList();

    return new PaginaResultado<>(figuritas, cantidadResultados, (int) Math.ceil( (double) cantidadResultados /limite), pagina);
  }

  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesConFiltros(
      FiguritasFiltro filtros, int pagina, int tamanioPagina) {
    Query query = new Query();
    /*query.addCriteria(
        //Filtros
    )
    */

    List<FiguritaIntercambiable> contenido = this.mongoTemplate.find(query, FiguritaIntercambiable.class);
    int count = Math.toIntExact(this.mongoTemplate.count(query, FiguritaIntercambiable.class));

    return new PaginaResultado<>(contenido, count, count/tamanioPagina, pagina);
  }
  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina) {
    return null; //??
  }

  @Override
  public List<FiguritaIntercambiable> buscarIntercambiablesPorFiguritaIds(List<String> figuritaIds) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("figurita").is(figuritaIds)
    );


    List<FiguritaIntercambiable> contenido = this.mongoTemplate.find(query, FiguritaIntercambiable.class);
    return contenido;
  }

  @Override
  public List<FiguritaIntercambiable> buscarIntercambiablesPorUsuarioId(String perfilId) {
    return null;
  }

  private int contarCampoEnColeccion(String colId, String campo, List<AggregationOperation> ops) {
    List<AggregationOperation> operaciones = new ArrayList<>();

    operaciones.add(Aggregation.match(Criteria.where("_id").is(colId)));
    operaciones.add(Aggregation.unwind(campo));
    operaciones.addAll(ops);
    operaciones.add(Aggregation.count().as("total"));

    Aggregation countAggregation = Aggregation.newAggregation(operaciones);

    AggregationResults<Document> countResult =
        mongoTemplate.aggregate(countAggregation, "colecciones", Document.class);

    Document countDoc = countResult.getUniqueMappedResult();
    return countDoc != null ? countDoc.getInteger("total") : 0;
  }

  private AggregationResults<Document> buscarCampoEnColeccion(String colId, String campo, List<AggregationOperation> ops, int pagina, int limite) {
    List<AggregationOperation> operaciones = new ArrayList<>();
    operaciones.add(Aggregation.match(Criteria.where("_id").is(colId)));
    operaciones.add(Aggregation.unwind(campo));
    operaciones.addAll(ops);
    operaciones.add(Aggregation.skip((long) (pagina - 1) * limite));
    operaciones.add(Aggregation.limit(limite));
    operaciones.add(Aggregation.replaceRoot(campo));

    Aggregation aggregation = Aggregation.newAggregation(operaciones);

    return mongoTemplate.aggregate(aggregation, "colecciones", Document.class);
  }
}
