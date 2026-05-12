package app.repositories.implMongo;

import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.repositories.RepositorioPerfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    return null;
  }

  public List<Perfil> buscarPorFiguritaFaltante(Figurita figurita) {
    return null;
  }

  public List<Perfil> buscarTodos() {
    return null;
  }

  public int contar() {
    return 0;
  }

  public void guardar(Perfil perfil) {
    mongoTemplate.save(perfil);
  }
}

