package app.repositories.impl;

import app.dto.filtros.FiguritasFiltro;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.repositories.RepositorioFiguritas;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFiguritasMongo implements RepositorioFiguritas {

  private static final String STATUS_EN_PROCESO = "EN_PROCESO";

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public Figurita buscarPorId(String id) {
    Figurita figurita = this.mongoTemplate.findById(id, Figurita.class);

    if (figurita == null) {
      throw new NotFoundException("Figurita no encontrada");
    }

    return figurita;
  }

  @Override
  public List<Figurita> buscarPorIds(List<String> ids) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("_id").in(ids)
    );
    return this.mongoTemplate.find(query, Figurita.class);
  }

  @Override
  public List<Figurita> buscarConFiltros(FiguritasFiltro filtros) {
    Query query = new Query();
    if (filtros.id() != null) {
      query.addCriteria(Criteria.where("_id").is(filtros.id()));
    }
    if (filtros.jugador() != null) {
      query.addCriteria(Criteria.where("jugador").regex(filtros.jugador(), "i"));
    }
    if (filtros.numero() != null) {
      query.addCriteria(Criteria.where("numero").is(filtros.numero()));
    }
    if (filtros.seleccion() != null) {
      query.addCriteria(Criteria.where("seleccion").regex(filtros.seleccion(), "i"));
    }

    int tamanioPagina = filtros.tamanioPaginaEfectivo();
    query.skip((long) filtros.paginaEfectiva() * tamanioPagina).limit(tamanioPagina);

    return this.mongoTemplate.find(query, Figurita.class);
  }

  @Override
  public void guardar(Figurita figurita) {
    this.mongoTemplate.save(figurita);
  }

  @Override
  public List<Figurita> buscarPendientes(Duration ttl) {
    return mongoTemplate.find(new Query(pendientesCriteria(ttl)), Figurita.class);
  }

  @Override
  public Figurita reclamarParaProcesamiento(String figuritaId, Duration ttl) {
    Query query = new Query(
        new Criteria().andOperator(
            Criteria.where("_id").is(figuritaId),
            pendientesCriteria(ttl)
        )
    );
    Update update = new Update()
        .set("imagenStatus", STATUS_EN_PROCESO)
        .set("imagenCreadoEn", LocalDateTime.now());

    return mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(false), Figurita.class);
  }

  private static Criteria pendientesCriteria(Duration ttl) {
    LocalDateTime expiradoAntes = LocalDateTime.now().minus(ttl);
    return new Criteria().orOperator(
        Criteria.where("imagenStatus").is(null),
        Criteria.where("imagenStatus").is(STATUS_EN_PROCESO)
                .and("imagenCreadoEn").lt(expiradoAntes)
    );
  }
}
