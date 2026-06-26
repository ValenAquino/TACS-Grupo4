package app.servicios.impl;

import app.MongoTestBase;
import app.dto.filtros.SugerenciasFiltro;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Sugerencia;
import app.model.entities.Usuario;
import app.repositories.impl.campos.CamposPerfil;
import app.servicios.ServicioJwt;
import app.servicios.ServicioSugerencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ServicioSugerenciaTest extends MongoTestBase {

  private ServicioSugerencia service;
  private Perfil usuario;
  private Perfil otro;
  private Figurita messi;

  @Mock
  private ServicioJwt jwt;

  @BeforeEach
  void setUp() {
    service = new ServicioSugerencia(repositorioSugerencias, repositorioPerfiles);
    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    Coleccion colec = new Coleccion("c-1");
    usuario = Perfil.builder()
        .id("1").usuario(user).nombre("Lucas")
        .coleccion(colec)
        .mediosDeContacto(telegram("@lucas"))
        .build();
    repositorioColecciones.guardar(colec);
    repositorioUsuarios.guardar(user);

    user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    colec = new Coleccion("c-2");
    otro = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .coleccion(colec)
        .mediosDeContacto(telegram("@sofía"))
        .build();
    repositorioColecciones.guardar(colec);
    repositorioUsuarios.guardar(user);
    repositorioPerfiles.guardar(usuario);
    repositorioPerfiles.guardar(otro);

    messi = Figurita.builder()
        .id("ARG-10")
        .numero(10)
        .jugador("Messi")
        .seleccion(Seleccion.ARGENTINA)
        .build();
    repositorioFiguritas.guardar(messi);
  }

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  private void generarSugerencias() {
    this.repositorioSugerencias.eliminacionProgramada();

    List<Perfil> perfiles = this.repositorioPerfiles.buscarTodos(new CamposPerfil(false));
    perfiles.forEach(perfil -> {

      List<Sugerencia> sugerencias = this.repositorioSugerencias.generarSugerencias(perfil);
      this.repositorioSugerencias.guardar(sugerencias);
    });
  }

  @Test
  void obtenerSugerencias_conCoincidencias_retornaSugerencias() {
    Figurita diMaria = Figurita.builder()
        .id("ARG-11")
        .numero(11)
        .jugador("Di María")
        .seleccion(Seleccion.ARGENTINA)
        .build();
    repositorioFiguritas.guardar(diMaria);
    usuario.getColeccion().agregarFaltante(messi);
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(diMaria, 2, List.of(MetodoIntercambio.INTERCAMBIO)));
    repositorioColecciones.guardar(usuario.getColeccion());

    Coleccion coleccionOtro = new Coleccion("c-3");
    coleccionOtro.getRepetidas().add(new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.INTERCAMBIO)));
    coleccionOtro.getFaltantes().add(diMaria);

    repositorioColecciones.guardar(coleccionOtro);

    Usuario user = new Usuario("u-3", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);
    Perfil otroConMessi = Perfil.builder()
        .id("3").usuario(user).nombre("Juan")
        .coleccion(coleccionOtro)
        .mediosDeContacto(telegram("@juan"))
        .build();
    repositorioPerfiles.guardar(otroConMessi);

    when(jwt.getPerfilId(any())).thenReturn("1");

    this.generarSugerencias();

    var resultado = service.obtenerSugerencias("1", new SugerenciasFiltro(1, 10));

    assertEquals(1, resultado.contenido().size());
  }

  @Test
  void obtenerSugerencias_sinCoincidencias_retornaListaVacia() {
    Figurita messi = Figurita.builder()
        .id("ARG-10")
        .numero(10)
        .jugador("Messi")
        .seleccion(Seleccion.ARGENTINA)
        .build();
    usuario.getColeccion().getFaltantes().add(messi);

    when(jwt.getPerfilId(any())).thenReturn("1");

    var resultado = service.obtenerSugerencias("1", new SugerenciasFiltro(1, 10));

    assertEquals(0, resultado.contenido().size());
  }
}
