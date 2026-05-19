package app.repositories.impl;

import app.model.entities.Calificacion;
import app.repositories.RepositorioCalificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioCalificacionesMongo implements RepositorioCalificacion {
  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Calificacion calificacion) {
    mongoTemplate.save(calificacion);
  }
}
