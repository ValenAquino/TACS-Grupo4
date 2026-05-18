package app.repositories.implMongo;

import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.Perfil;
import app.model.entities.Sugerencia;
import app.repositories.RepositorioPerfiles;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public List<Sugerencia> generarSugerencias(Coleccion coleccion, String idPerfilObjetivo) {
    Set<ObjectId> faltantesObjetivo = coleccion.getFaltantes()
        .stream()
        .map(f -> new ObjectId(f.getId()))
        .collect(Collectors.toSet());

    Query query = new Query(
        new Criteria().andOperator(
            Criteria.where("_id").ne(new ObjectId(idPerfilObjetivo)),
            Criteria.where("coleccion.repetidas.$id").in(faltantesObjetivo)
        )
    );

    try (Stream<Perfil> cursor = mongoTemplate.stream(query, Perfil.class)) {
      return cursor
          .map(perfil -> {
            Set<ObjectId> faltantesPerfil = perfil.getColeccion().getFaltantes()
                .stream()
                .map(f -> new ObjectId(f.getId()))
                .collect(Collectors.toSet());

            List<Figurita> sugeridas = perfil.getColeccion().getRepetidas().stream()
                .map(FiguritaIntercambiable::getFigurita)
                .filter(f -> faltantesObjetivo.contains(new ObjectId(f.getId())))
                .toList();

            List<Figurita> necesarias = coleccion.getRepetidas().stream()
                .map(FiguritaIntercambiable::getFigurita)
                .filter(f -> faltantesPerfil.contains(new ObjectId(f.getId())))
                .toList();

            return new Sugerencia(perfil, sugeridas, necesarias);
          })
          .filter(s -> !s.getFiguritasSugeridas().isEmpty() && !s.getFiguritasNecesarias().isEmpty())
          .toList();
    }
  }
}

