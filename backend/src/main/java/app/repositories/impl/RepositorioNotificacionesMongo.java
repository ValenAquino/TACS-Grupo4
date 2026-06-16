package app.repositories.impl;

import app.model.entities.Perfil;
import app.model.notificador.Notificacion;
import app.repositories.RepositorioNotificaciones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.mongodb.core.query.Update;
import org.bson.Document;
import org.bson.types.ObjectId;

@Repository
public class RepositorioNotificacionesMongo implements RepositorioNotificaciones {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public void guardar(Notificacion notificacion) {
    this.mongoTemplate.save(notificacion);
  }

  @Override
  public void guardar(List<Notificacion> notificaciones) {
      for (Notificacion n : notificaciones) {
          Query query = new Query(Criteria.where("_id").is(n.getId()));
          Update update = new Update().set("leida", n.isLeida());
          mongoTemplate.updateFirst(query, update, Notificacion.class);
      }
  }

    @Override
    public List<Notificacion> buscarPorPerfil(Perfil perfil) {
        Object refId = perfil.getId();
        if (refId instanceof String && ObjectId.isValid((String) refId)) {
            refId = new ObjectId((String) refId);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("perfil").is(new Document("$ref", "perfiles").append("$id", refId))
        );
        return mongoTemplate.find(query, Notificacion.class);
    }

  @Override
  public List<Notificacion> buscarPorPerfilFechaDesc(String perfilId) {

      Object refId;

      if (ObjectId.isValid(perfilId)) {
          refId = new ObjectId(perfilId);
      } else {
          refId = perfilId;
      }

      Query query = new Query();
      query.addCriteria(Criteria.where("perfil").is(new Document("$ref", "perfiles").append("$id", refId))
      );
      query.with(Sort.by(Sort.Direction.DESC, "mensaje.fecha"));
      return mongoTemplate.find(query, Notificacion.class);
  }
}
