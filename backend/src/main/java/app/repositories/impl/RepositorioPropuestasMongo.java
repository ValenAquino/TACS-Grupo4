package app.repositories.impl;

import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.propuesta.PropuestasDto;
import app.model.entities.Calificacion;
import app.model.entities.Propuesta;
import app.repositories.RepositorioPropuestas;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RepositorioPropuestasMongo implements RepositorioPropuestas {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void guardar(Propuesta propuesta) {
        this.mongoTemplate.save(propuesta);
    }

    @Override
    public PaginaResultado<Propuesta> buscarPorAutorId(String perfilId, PropuestasFiltro filtros) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("autor.id").is(perfilId)
        );

//        if (filtros.estado() != null) {
//            query.addCriteria(
//                Criteria.expr(
//                    MongoExpression.create(
//                        "{ $eq: [ " +
//                            "{ $arrayElemAt: ['$estado.estadoProceso', -1] }, " +
//                            "'" + filtros.estado().name() + "'" +
//                            "] }"
//                    )
//                )
//            );
//        }

        long count = mongoTemplate.count(query, Propuesta.class);

        query.skip((long) filtros.pagina() * filtros.limite());
        query.limit(filtros.limite());

        List<Propuesta> contenido =
            mongoTemplate.find(query, Propuesta.class);

        return new PaginaResultado<>(
            contenido,
            count,
            (int) Math.ceil((double) count / filtros.limite()),
            filtros.pagina()
        );
    }

    @Override
    public PaginaResultado<Propuesta> buscarPorDestinatarioId(String perfilId, PropuestasFiltro filtros) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("destinatario.id").is(perfilId)
        );

        long count = mongoTemplate.count(query, Propuesta.class);

        query.skip((long) filtros.pagina() * filtros.limite());
        query.limit(filtros.limite());

        List<Propuesta> contenido =
            mongoTemplate.find(query, Propuesta.class);

        return new PaginaResultado<>(
            contenido,
            count,
            (int) Math.ceil((double) count / filtros.limite()),
            filtros.pagina()
        );
    }

    @Override
    public List<Propuesta> buscarTodos() {
        return this.mongoTemplate.findAll(Propuesta.class);
    }

    @Override
    public Propuesta buscarPorId(String id){
        Propuesta propuesta = this.mongoTemplate.findById(id, Propuesta.class);

        if(propuesta == null) {
            throw new RuntimeException("Propuesta no encontrada");
        }
        return propuesta;
    }

    @Override
    public int contar() {
        return (int) this.mongoTemplate.count(new Query(), Propuesta.class);
    }
}