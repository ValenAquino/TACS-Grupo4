package app.repositories.impl;

import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.repositories.RepositorioPerfiles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPerfilesEnMemoria {

    private final Map<String, Perfil> storage = new HashMap<>();

    public Perfil buscarPorId(String id) {
        if(!storage.containsKey(id)) {
            throw new NotFoundException("Perfil no encontrado");
        }
        return storage.get(id);
    }

    public Perfil buscarPorUsuarioId(String usuarioId) {
        return storage.values().stream()
            .filter(p -> p.getUsuario().getId().equals(usuarioId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Perfil no encontrado para el usuario: " + usuarioId));
    }

    public List<Perfil> buscarPorFiguritaFaltante(Figurita figurita) {
        return this.storage.values()
            .stream()
            .filter(u -> u.getColeccion().tieneFaltante(figurita))
            .toList();
    }

    public List<Perfil> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    public int contar() {
        return storage.size();
    }

    public void guardar(Perfil perfil) {
        if(!storage.containsKey(perfil.getId())) {
            storage.put(perfil.getId(), perfil);
        }
    }
}