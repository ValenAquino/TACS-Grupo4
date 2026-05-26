package app.repositories;

import app.model.entities.Perfil;
import app.model.notificador.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositorioNotificaciones extends MongoRepository<Notificacion, String> {
    List<Notificacion> findByPerfilIdOrderByMensajeFechaDesc(String perfilId);

    default void guardar(Notificacion notificacion) {
        save(notificacion);
    }

    default List<Notificacion> buscarPorPerfil(Perfil perfil) {
        return findByPerfilIdOrderByMensajeFechaDesc(perfil.getId());
    }
}

