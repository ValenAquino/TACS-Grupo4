package app.repositories.implMongo;

import app.model.entities.Usuario;
import app.repositories.RepositorioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioUsuariosMongo implements RepositorioUsuarios {

  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Usuario usuario) {
    this.mongoTemplate.save(usuario);
  }
}
