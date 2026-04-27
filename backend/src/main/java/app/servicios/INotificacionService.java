package app.servicios;

import app.model.entities.Perfil;
import java.util.List;

public interface INotificacionService {

    /**
     * Persiste una notificación por cada perfil interesado.
     * Por ahora almacena en memoria; en el futuro se integrará con el adaptador de Telegram.
     */
    void notificarInteresados(List<Perfil> interesados, String cuerpo);
}
