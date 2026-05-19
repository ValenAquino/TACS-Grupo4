package app.repositories.implMongo;

import app.dto.calificaciones.CalificacionesDto;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.repositories.RepositorioPerfiles;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

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
    return null;
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

  @Override
  public CalificacionesDto buscarCalificaciones(String id, Integer pagina, Integer limite) {
    return null;
  }
}

