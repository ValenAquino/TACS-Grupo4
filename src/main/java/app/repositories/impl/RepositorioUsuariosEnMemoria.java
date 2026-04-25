package app.repositories.impl;

import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.repositories.RepositorioUsuarios;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioUsuariosEnMemoria implements RepositorioUsuarios {

    private final Map<String, Perfil> storage = new HashMap<>();

    @Override
    public Perfil buscarPorId(String id) {
        if(!storage.containsKey(id)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        return storage.get(id);
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
    public void guardar(Perfil usuario) {
        if(!storage.containsKey(usuario.getId())) {
            storage.put(usuario.getId(), usuario);
        }
    }
}