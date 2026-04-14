package app.servicios;

import app.model.entities.Usuario;
import java.util.List;

public interface INotificacionService {
  public void notificarInteresados(List<Usuario> interesados, String cuerpo);
}
