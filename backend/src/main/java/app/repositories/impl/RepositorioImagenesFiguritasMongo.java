package app.repositories.impl;

import app.model.entities.ImagenFigurita;
import app.repositories.RepositorioImagenesFiguritas;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioImagenesFiguritasMongo implements RepositorioImagenesFiguritas {

  private static final String STATUS_EN_PROCESO = "EN_PROCESO";

  private final MongoTemplate mongoTemplate;

  public RepositorioImagenesFiguritasMongo(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void guardar(ImagenFigurita imagen) {
    mongoTemplate.save(imagen);
  }

  @Override
  public Optional<ImagenFigurita> buscarPorId(String id) {
    return Optional.ofNullable(mongoTemplate.findById(id, ImagenFigurita.class));
  }

  @Override
  public ImagenFigurita iniciarProcesamiento(String figuritaId) {
    Query query = new Query(Criteria.where("_id").is(figuritaId));

    Update update = new Update()
        .setOnInsert("figuritaId", figuritaId)
        .setOnInsert("status", STATUS_EN_PROCESO)
        .setOnInsert("creadoEn", LocalDateTime.now());

    FindAndModifyOptions opciones = FindAndModifyOptions.options()
        .upsert(true)
        .returnNew(false);

    return mongoTemplate.findAndModify(query, update, opciones, ImagenFigurita.class);
  }

  @Override
  public ImagenFigurita retomarProcesamiento(String figuritaId) {
    Query query = new Query(
        Criteria.where("_id").is(figuritaId).and("status").is(STATUS_EN_PROCESO)
    );

    Update update = new Update()
        .set("status", STATUS_EN_PROCESO)
        .set("creadoEn", LocalDateTime.now());

    FindAndModifyOptions retornar_anterior = FindAndModifyOptions
        .options()
        .upsert(false)
        .returnNew(false);

    return mongoTemplate.findAndModify(query, update, retornar_anterior, ImagenFigurita.class);
  }
}
