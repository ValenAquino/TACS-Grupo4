package app.repositories.implMongo;

import app.model.entities.Usuario;
import app.repositories.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioUsuariosMongo implements RepositorioUsuario {

  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Usuario usuario) {
    this.mongoTemplate.save(usuario);
  }
}
