package app.model.notificador;

import app.model.entities.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificadorTest {

    private final Perfil perfil = new Perfil("1", null, "Test", null, List.of(), List.of());
    private final Mensaje mensaje = new Mensaje("cuerpo", LocalDateTime.now());

    @Test
    void notificacion_constructor_asignaAtributos() {
        Notificacion notificacion = new Notificacion(mensaje, perfil);
        assertEquals(mensaje, notificacion.getMensaje());
        assertEquals(perfil, notificacion.getUsuario());
    }

    @Test
    void notificacion_setters() {
        Notificacion notificacion = new Notificacion(mensaje, perfil);
        Mensaje otro = new Mensaje("otro", LocalDateTime.now());
        notificacion.setMensaje(otro);
        notificacion.setId("id-1");
        assertEquals(otro, notificacion.getMensaje());
        assertEquals("id-1", notificacion.getId());
    }

    @Test
    void notificador_enviarNotificacion_delegaAlAdapter() {
        AdapterNotificacion mockAdapter = mock(AdapterNotificacion.class);
        Notificador notificador = new Notificador(mockAdapter);
        notificador.enviarNotificacion(mensaje, perfil);
        verify(mockAdapter).notificar(mensaje, perfil);
    }

    @Test
    void adapterTelegram_notificar_noLanzaExcepcion() {
        AdapterTelegram adapter = new AdapterTelegram();
        assertDoesNotThrow(() -> adapter.notificar(mensaje, perfil));
    }
}
