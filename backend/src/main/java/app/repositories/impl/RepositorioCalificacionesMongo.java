package app.repositories.impl;

import app.dto.paginacion.PaginaResultado;
import app.model.entities.Calificacion;
import app.model.entities.FiguritaIntercambiable;
import app.repositories.RepositorioCalificacion;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RepositorioCalificacionesMongo implements RepositorioCalificacion {
  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Calificacion calificacion) {
    mongoTemplate.save(calificacion);
  }

  @Override
  public PaginaResultado<Calificacion> buscarPorPerfil(
      String perfilId,
      Integer pagina,
      Integer limite
  ) {

    Query query = new Query();

    query.addCriteria(
        Criteria.where("autor.$id").is(perfilId)
    );

    long count = mongoTemplate.count(query, Calificacion.class);

    query.skip((long) pagina * limite);
    query.limit(limite);

    List<Calificacion> contenido =
        mongoTemplate.find(query, Calificacion.class);

    return new PaginaResultado<>(
        contenido,
        count,
        (int) Math.ceil((double) count / limite),
        pagina
    );
  }
}
