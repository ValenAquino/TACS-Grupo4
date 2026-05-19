package app.repositories.implMongo;

import app.model.entities.Perfil;
import app.model.notificador.Notificacion;
import app.repositories.RepositorioNotificaciones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RepositorioNotificacionesMongo implements RepositorioNotificaciones {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public void guardar(Notificacion notificacion) {
    this.mongoTemplate.save(notificacion);
  }

  @Override
  public List<Notificacion> buscarPorPerfil(Perfil perfil) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("perfil").is(perfil)
    );

    return this.mongoTemplate.find(query, Notificacion.class);
  }
}
