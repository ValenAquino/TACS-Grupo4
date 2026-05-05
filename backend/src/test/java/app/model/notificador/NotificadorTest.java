package app.model.notificador;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import app.model.entities.Coleccion;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificadorTest {

  private Perfil perfil() {
    return new Perfil("p-1", new Usuario("u-1", Rol.USUARIO), "Lucas",
        new Coleccion(), List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@lucas")),
        new ArrayList<>());
  }

  @Test
  void enviarNotificacion_delegaAlAdapter() {
    AdapterNotificacion adapter = mock(AdapterNotificacion.class);
    Notificador notificador = new Notificador(adapter);
    Mensaje mensaje = new Mensaje("Hola", LocalDateTime.now());
    Perfil perfil = perfil();

    notificador.enviarNotificacion(mensaje, perfil);

    verify(adapter).notificar(mensaje, perfil);
  }

  @Test
  void notificacion_guardaMensajeYUsuario() {
    Mensaje mensaje = new Mensaje("Hola", LocalDateTime.now());
    Perfil perfil = perfil();

    Notificacion notificacion = new Notificacion(mensaje, perfil);

    assertEquals(mensaje, notificacion.getMensaje());
    assertEquals(perfil, notificacion.getUsuario());
  }

  @Test
  void adapterTelegram_notificar_noLanzaExcepcion() {
    AdapterTelegram adapter = new AdapterTelegram();
    Mensaje mensaje = new Mensaje("Hola", LocalDateTime.now());

    assertDoesNotThrow(
        () -> adapter.notificar(mensaje, perfil())
    );
  }
}
