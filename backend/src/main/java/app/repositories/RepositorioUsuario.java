package app.repositories;

import app.model.entities.Usuario;


public interface RepositorioUsuario {
  void guardar(Usuario usuario);
  Usuario buscarPorNombre(String nombre);
}
