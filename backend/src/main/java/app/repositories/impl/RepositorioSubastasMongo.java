package app.repositories.impl;

import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.exceptions.NotFoundException;
import app.model.entities.Calificacion;
import app.model.entities.Perfil;
import app.model.entities.Subasta;
import app.repositories.RepositorioSubastas;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RepositorioSubastasMongo implements RepositorioSubastas {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public PaginaResultado<Subasta> buscarPorAutor(String perfilId, Integer pagina, Integer limite) {
    Query query = new Query();

    query.addCriteria(
        Criteria.where("autor.$id").is(new ObjectId(perfilId))
    );

    long count = mongoTemplate.count(query, Subasta.class);

    query.skip((long) pagina * limite);
    query.limit(limite);

    List<Subasta> contenido =
        mongoTemplate.find(query, Subasta.class);

    return new PaginaResultado<>(
        contenido,
        count,
        (int) Math.ceil((double) count / limite),
        pagina
    );
  }

  @Override
  public PaginaResultado<Subasta> buscarTodos(SubastasFiltro filtros) {
    Query query = new Query();

    if(filtros.autorId() != null) {
      query.addCriteria(
          Criteria.where("autor").is(filtros.autorId())
      );
    }

    if ("ACTIVA".equals(filtros.estado())) {
      Criteria.expr(
          ComparisonOperators.Lt.valueOf("fechaInicio")
              .lessThan("fechaCierre")
      );
    }

    if (filtros.participanteId() != null) {
      query.addCriteria(
          Criteria.where("ofertas.autor").is(filtros.participanteId())
      );
    }

    long count = mongoTemplate.count(query, Subasta.class);

    query.skip((long) (filtros.pagina() - 1) * filtros.limite());
    query.limit(filtros.limite());

    List<Subasta> contenido = mongoTemplate.find(query, Subasta.class);

    return new PaginaResultado<>(
        contenido,
        count,
        (int) Math.ceil((double) count / filtros.limite()),
        filtros.pagina()
    );
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
