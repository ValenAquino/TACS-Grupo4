package app.repositories.impl;

import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.propuesta.PropuestasDto;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Propuesta;
import app.repositories.RepositorioPropuestas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPropuestasEnMemoria implements RepositorioPropuestas {

    private final Map<String, Propuesta> storage = new HashMap<>();

    @Override
    public PropuestasDto buscarPorAutorId(String userId, PropuestasFiltro filtros) {
        List<Propuesta> propuestas = storage.values().stream()
                .filter(p -> p.getAutor().getUsuario().getId().equals(userId))
                .toList();

        int resultados = propuestas.size();

        int paginaActual = filtros.pagina();
        int limite = filtros.limite();
        int offset = (paginaActual - 1) * limite;

        List<PropuestaDto> data = propuestas.stream()
            .skip(offset)
            .limit(limite)
            .map(PropuestaDto::new)
            .toList();

        int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

        return new PropuestasDto(data, resultados, paginaActual, paginasTotales);
    }

    @Override
    public PropuestasDto buscarPorDestinatarioId(String userId, PropuestasFiltro filtros) {
        List<Propuesta> propuestas =  storage.values().stream()
                .filter(p -> p.getDestinatario().getUsuario().getId().equals(userId))
                .toList();

        int resultados = propuestas.size();

        int paginaActual = filtros.pagina();
        int limite = filtros.limite();
        int offset = (paginaActual - 1) * limite;

        List<PropuestaDto> data = propuestas.stream()
            .skip(offset)
            .limit(limite)
            .map(PropuestaDto::new)
            .toList();

        int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

        return new PropuestasDto(data, resultados, paginaActual, paginasTotales);
    }

    @Override
    public List<Propuesta> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void guardar(Propuesta propuesta) {

      this.storage.putIfAbsent(propuesta.getId(), propuesta);
    }

    @Override
    public Propuesta buscarPorId(String id){
        Propuesta propuesta = storage.get(id);

        if (propuesta == null) {
            throw new RuntimeException("Propuesta no encontrada");
        }
        return propuesta;
    }

    @Override
    public int contar() {
        return storage.size();
    }

}