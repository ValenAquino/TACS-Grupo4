package app.repositories.impl;

import app.model.entities.Coleccion;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Usuario;
import app.model.notificador.Mensaje;
import app.model.notificador.Notificacion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositorioNotificacionesEnMemoriaTest {

  private RepositorioNotificacionesEnMemoria repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioNotificacionesEnMemoria();
  }

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  @Test
  void buscarNotificacionesDeUnUsuario() {
    LocalDateTime fecha = LocalDateTime.now();
    Mensaje mensaje = new Mensaje("Mensaje1", fecha);

    Usuario user = new Usuario("u-1", Rol.USUARIO,"lucas", "fiscella");
    Perfil perfil1 = Perfil.builder()
        .id("1").usuario(user).nombre("Juan")
        .mediosDeContacto(telegram("@juan"))
        .build();

    user = new Usuario("u-2", Rol.USUARIO,"lucas", "fiscella");
    Perfil perfil2 = Perfil.builder()
        .id("2").usuario(user)
        .nombre("Miguel").mediosDeContacto(telegram("@miguel"))
        .build();

    Notificacion notificacion1 = new Notificacion(mensaje, perfil1);
    Notificacion notificacion2 = new Notificacion(mensaje, perfil1);
    Notificacion notificacion3 = new Notificacion(mensaje, perfil1);
    Notificacion notificacion4 = new Notificacion(mensaje, perfil2);
    Notificacion notificacion5 = new Notificacion(mensaje, perfil2);

    repositorio.guardar(notificacion1);
    repositorio.guardar(notificacion2);
    repositorio.guardar(notificacion3);
    repositorio.guardar(notificacion4);
    repositorio.guardar(notificacion5);

    assertEquals(3, repositorio.buscarPorPerfil(perfil1).size());
  }
}