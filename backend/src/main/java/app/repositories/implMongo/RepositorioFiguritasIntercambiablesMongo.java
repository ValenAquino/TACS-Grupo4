package app.repositories.implMongo;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritasIntercambiables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioFiguritasIntercambiablesMongo implements RepositorioFiguritasIntercambiables {
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarConFiltros(
                  FiguritasFiltro filtros, int pagina, int tamanioPagina) {
    Query query = new Query();
    /*query.addCriteria(
        //Filtros
    )
    */

    List<FiguritaIntercambiable> contenido = this.mongoTemplate.find(query, FiguritaIntercambiable.class);
    int count = Math.toIntExact(this.mongoTemplate.count(query, FiguritaIntercambiable.class));

    return new PaginaResultado<>(contenido, count, count/tamanioPagina, pagina);
  }
  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina) {
    return null; //??
  }

  @Override
  public List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds) {
    Query query = new Query();
    query.addCriteria(
        Criteria.where("figurita").is(figuritaIds)
    );


    List<FiguritaIntercambiable> contenido = this.mongoTemplate.find(query, FiguritaIntercambiable.class);
  }

  @Override
  public List<FiguritaIntercambiable> buscarPorUsuarioId(String perfilId) {
   return null;
  }

  @Override
  public void guardar(FiguritaIntercambiable figuritaIntercambiable) {
    this.mongoTemplate.save(figuritaIntercambiable);
  }
}
