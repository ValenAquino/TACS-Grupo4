package app.repositories.implMongo;

import app.dto.FaltantesDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.RepetidasDto;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

  public RepetidasDto buscarRepetidas(String colId, RepetidasFiltro filtros){

    int pagina = filtros.pagina();
    int limite = filtros.limite();

    Aggregation countAggregation = Aggregation.newAggregation(

        Aggregation.match(
            Criteria.where("id").is(colId)
        ),

        Aggregation.unwind("repetidas"),

        Aggregation.match(
            Criteria.where("repetidas.metodos")
                .is(filtros.metodoIntercambio())
        ),

        Aggregation.count().as("total")
    );

    AggregationResults<Document> countResult =
        mongoTemplate.aggregate(
            countAggregation,
            Coleccion.class,
            Document.class
        );

    Document doc = countResult.getUniqueMappedResult();

    int total = doc != null
        ? doc.getInteger("total")
        : 0;

    Aggregation aggregation = Aggregation.newAggregation(

        Aggregation.match(
            Criteria.where("id").is(colId)
        ),

        Aggregation.unwind("repetidas"),

        Aggregation.match(
            Criteria.where("repetidas.metodos")
                .is(filtros.metodoIntercambio())
        ),

        Aggregation.skip((long) (pagina - 1) * limite),

        Aggregation.limit(limite),

        Aggregation.replaceRoot("repetidas")
    );

    AggregationResults<FiguritaIntercambiable> resultado =
        mongoTemplate.aggregate(
            aggregation,
            Coleccion.class,
            FiguritaIntercambiable.class
        );

    List<FiguritaIntercambiable> repetidas =
        resultado.getMappedResults();

    List<FiguritaIntercambiableDto> figDtos = repetidas.stream().map(FiguritaIntercambiableDto::new).toList();

    return new RepetidasDto(figDtos,0,0,total,pagina, (int) Math.ceil((double) total/pagina));
  }

  public FaltantesDto buscarFaltantes(String colId, FaltantesFiltro filtros){
    return null;
  }
}
