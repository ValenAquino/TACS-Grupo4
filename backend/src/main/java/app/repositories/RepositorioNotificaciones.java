package app.repositories;

import app.model.entities.Perfil;
import app.model.notificador.Notificacion;

import java.util.List;

public interface RepositorioNotificaciones {
    List<Notificacion> buscarPorPerfilFechaDesc(String perfilId);

    void guardar(Notificacion notificacion);

    List<Notificacion> buscarPorPerfil(Perfil perfil);
}