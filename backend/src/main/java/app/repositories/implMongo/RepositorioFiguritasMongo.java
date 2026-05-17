package app.repositories.implMongo;

import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioFiguritasMongo implements RepositorioFiguritas {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public Figurita buscarPorId(String id) {

    return null;
  }

  @Override
  public List<Figurita> buscarConFiltros(FiguritasFiltro filtros) {

    return null;
  }

  @Override
  public void guardar(Figurita figurita) {
    this.mongoTemplate.save(figurita);
  }
}
