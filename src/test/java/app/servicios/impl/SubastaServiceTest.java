package app.servicios.impl;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Seleccion;
import app.model.entities.Perfil;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import app.repositories.impl.RepositorioNotificacionesEnMemoria;
import app.servicios.ISubastaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubastaServiceTest {

  @Mock private RepositorioPerfiles repositorioUsuarios;
  @Mock private RepositorioPropuestas repositorioPropuestas;
  @Mock private RepositorioSubastas repositorioSubastas;
  @Mock private RepositorioFiguritas repositorioFiguritas;

  private RepositorioNotificaciones repositorioNotificaciones;
  private ISubastaService service;

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  @BeforeEach
  void setUp() {
    this.repositorioNotificaciones = new RepositorioNotificacionesEnMemoria();
    NotificacionService serviceNotificacion = new NotificacionService(repositorioNotificaciones);

    service = new SubastaServiceImpl(repositorioSubastas, repositorioUsuarios,
        repositorioFiguritas, repositorioPropuestas, serviceNotificacion);
  }

  @Test
  void crearSubastaNotificaUsuarios() {
    Figurita messi   = new Figurita("ARG-10", 10, "Messi",    Seleccion.ARGENTINA);

    Coleccion coleccionSinMessi = new Coleccion();
    coleccionSinMessi.getFaltantes().add(messi);
    Perfil usuarioSinMessi = new Perfil("u-1", "Lucas", coleccionSinMessi, telegram("@lucas"), new ArrayList<>());

    Coleccion coleccionRepetidos = new Coleccion();
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, new ArrayList<>()));
    Perfil sofia = new Perfil("u-2", "Sofía", coleccionRepetidos, telegram("@sofia"), new ArrayList<>());

    LocalDateTime fechaInicio = LocalDateTime.now();

    when(repositorioUsuarios.buscarPorId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
    when(repositorioUsuarios.buscarPorFiguritaFaltante(messi)).thenReturn(List.of(usuarioSinMessi));

    service.crearSubasta(sofia.getId(), fechaInicio, fechaInicio.plusMinutes(30), "ARG-10");

    assertEquals(1, repositorioNotificaciones.buscarPorUsuario(usuarioSinMessi).size());
  }

  @Test
  void crearSubastaNoNotificaUsuarios() {
    Figurita messi   = new Figurita("ARG-10", 10, "Messi",    Seleccion.ARGENTINA);
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA);

    Coleccion coleccionSinMessi = new Coleccion();
    coleccionSinMessi.getFaltantes().add(messi);
    Perfil usuarioSinMessi = new Perfil("u-1", "Lucas", coleccionSinMessi, telegram("@lucas"), new ArrayList<>());

    Coleccion coleccionRepetidos = new Coleccion();
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, new ArrayList<>()));
    Perfil sofia = new Perfil("u-2", "Sofía", coleccionRepetidos, telegram("@sofia"), new ArrayList<>());

    LocalDateTime fechaInicio = LocalDateTime.now();

    when(repositorioUsuarios.buscarPorId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-11")).thenReturn(diMaria);
    when(repositorioUsuarios.buscarPorFiguritaFaltante(diMaria)).thenReturn(List.of());

    service.crearSubasta(sofia.getId(), fechaInicio, fechaInicio.plusMinutes(30), "ARG-11");

    assertEquals(0, repositorioNotificaciones.buscarPorUsuario(usuarioSinMessi).size());
  }
}