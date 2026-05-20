package app.repositories.impl;

import app.dto.filtros.PropuestasFiltro;
import app.dto.propuesta.PropuestasDto;
import app.model.entities.Propuesta;
import app.repositories.RepositorioPropuestas;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<Propuesta> buscarPorAutorId(String perfilId) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("autor").is(perfilId)
        );
        return this.mongoTemplate.find(query, Propuesta.class);
    }


    public List<Propuesta> buscarPorDestinatarioId(String perfilId) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("destinatario").is(perfilId)
        );
        return this.mongoTemplate.find(query, Propuesta.class);
    }


    @Override
    public PropuestasDto buscarPorAutorId(String userId, PropuestasFiltro filtros) {
        return null;
    }

    @Override
    public PropuestasDto buscarPorDestinatarioId(String userId, PropuestasFiltro filtros) {
        return null;
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