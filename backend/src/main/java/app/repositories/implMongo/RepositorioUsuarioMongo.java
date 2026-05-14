package app.repositories.implMongo;

import app.exceptions.NotFoundException;
import app.model.entities.Usuario;
import app.repositories.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Repository
public class RepositorioUsuarioMongo implements RepositorioUsuario {

  @Autowired
  private MongoTemplate mongoTemplate;

  public void guardar(Usuario usuario) {
    this.mongoTemplate.save(usuario);
  }

  public Usuario buscarPorNombre(String nombre) {

    Query query = new Query();

    query.addCriteria(
        Criteria.where("nombre").is(nombre)
    );

    Usuario usuario = mongoTemplate.findOne(query, Usuario.class);

    if(usuario == null) {
      throw new NotFoundException("Usuario no encontrado");
    }

    return usuario;
  }
}
