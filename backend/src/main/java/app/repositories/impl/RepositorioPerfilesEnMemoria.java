package app.repositories.impl;

import app.dto.CalificacionDto;
import app.dto.calificaciones.CalificacionesDto;
import app.exceptions.NotFoundException;
import app.model.entities.Calificacion;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.repositories.RepositorioPerfiles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPerfilesEnMemoria implements RepositorioPerfiles {

    private final Map<String, Perfil> storage = new HashMap<>();

    @Override
    public Perfil buscarPorId(String id) {
        if(!storage.containsKey(id)) {
            throw new NotFoundException("Perfil no encontrado");
        }
        return storage.get(id);
    }

    @Override
    public Perfil buscarPorUsuarioId(String usuarioId) {
        return storage.values().stream()
            .filter(p -> p.getUsuario().getId().equals(usuarioId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Perfil no encontrado para el usuario: " + usuarioId));
    }

    @Override
    public List<Perfil> buscarPorFiguritaFaltante(Figurita figurita) {
        return this.storage.values()
            .stream()
            .filter(u -> u.getColeccion().tieneFaltante(figurita))
            .toList();
    }

    @Override
    public List<Perfil> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public int contar() {
        return storage.size();
    }

    @Override
    public void guardar(Perfil perfil) {
        if(!storage.containsKey(perfil.getId())) {
            storage.put(perfil.getId(), perfil);
        }
    }

    @Override
    public CalificacionesDto buscarCalificaciones(String id, Integer pagina, Integer limite) {
        Perfil perfil = this.buscarPorId(id);

        List<Calificacion> calificaciones = perfil.getCalificaciones();

        int resultados = calificaciones.size();

        int offset = (pagina - 1) * limite;

        List<CalificacionDto> data = calificaciones.stream()
            .skip(offset)
            .limit(limite)
            .map(c -> new CalificacionDto(c, perfil.obtenerCalificacionMedia()))
            .toList();

        int paginasTotales = (resultados + limite - 1) / limite;

        return new CalificacionesDto(data, resultados, pagina, paginasTotales);
    }
}