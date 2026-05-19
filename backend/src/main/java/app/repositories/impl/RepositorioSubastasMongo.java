package app.repositories.impl;

import app.exceptions.NotFoundException;
import app.model.entities.Perfil;
import app.model.entities.Subasta;
import app.repositories.RepositorioSubastas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RepositorioSubastasMongo implements RepositorioSubastas {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<Subasta> buscarPorAutorUsuarioId(String userId) {
    Query queryPerfil = new Query(Criteria.where("usuario.id").is(userId));
    Perfil perfil = mongoTemplate.findOne(queryPerfil, Perfil.class);

    if (perfil == null) return List.of();

    Query querySubastas = new Query(
        Criteria.where("autor.$id").is(perfil.getId())
    );
    return mongoTemplate.find(querySubastas, Subasta.class);
  }

  @Override
  public List<Subasta> buscarTodos() {
    return this.mongoTemplate.findAll(Subasta.class);
  }

  @Override
  public int contar() {
    return (int) this.mongoTemplate.count(new Query(),Subasta.class);
  }

  @Override
  public Subasta buscarPorId(String id) {
    Subasta subasta = this.mongoTemplate.findById(id, Subasta.class);

    if(subasta == null) {
      throw new NotFoundException("Subasta no encontrada");
    }
    return subasta;
  }

  @Override
  public List<Subasta> buscarDondeParticipa(String userId) {
    Query queryPerfil = new Query(Criteria.where("usuario.id").is(userId));
    Perfil perfil = mongoTemplate.findOne(queryPerfil, Perfil.class);

    if (perfil == null) return List.of();

    Query querySubastas = new Query(
        Criteria.where("ofertas").elemMatch(
            Criteria.where("$id").is(perfil.getId())
        )
    );
    return mongoTemplate.find(querySubastas, Subasta.class);
  }

  @Override
  public void guardar(Subasta subasta) {
    this.mongoTemplate.save(subasta);
  }
}
