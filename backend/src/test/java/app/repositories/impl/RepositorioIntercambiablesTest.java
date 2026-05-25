package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.MongoTestBase;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Usuario;
import app.dto.filtros.FiguritasFiltro;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioIntercambiablesTest extends MongoTestBase {
  private FiguritaIntercambiable intercambiableMessi;
  private FiguritaIntercambiable intercambiableMbappe;
  private Coleccion coleccion;

  @BeforeEach
  void setUp() {
    coleccion = new Coleccion("c-1");
    Coleccion coleccionDos = new Coleccion("c-2");
    Figurita messi = Figurita.builder()
        .id("ARG-10")
        .numero(10)
        .jugador("Messi")
        .seleccion(Seleccion.ARGENTINA)
        .posicion("Delantero")
        .build();
    Figurita mbappe = Figurita.builder()
        .id("FRA-10")
        .numero(10)
        .jugador("Mbappé")
        .seleccion(Seleccion.FRANCIA)
        .posicion("Delantero")
        .build();
    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(mbappe);

    repositorioColecciones.guardar(coleccion);
    repositorioColecciones.guardar(coleccionDos);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    Perfil perfilUno = Perfil.builder()
        .id("perfil-1").usuario(user).nombre("Lucas")
        .coleccion(coleccion)
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@lucas")))
        .build();
    user = new Usuario("u-2", Rol.USUARIO, "adas", "da");
    Perfil perfilDos = Perfil.builder()
        .id("perfil-2").usuario(user).nombre("adada")
        .coleccion(coleccionDos)
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@adas")))
        .build();
    repositorioPerfiles.guardar(perfilUno);
    repositorioPerfiles.guardar(perfilDos);
    intercambiableMessi = new FiguritaIntercambiable(messi, 3, List.of(MetodoIntercambio.INTERCAMBIO), perfilUno.getId());
    intercambiableMbappe = new FiguritaIntercambiable(mbappe, 2, List.of(MetodoIntercambio.SUBASTA), perfilDos.getId());
    coleccion.agregarRepetida(intercambiableMessi);
    coleccion.agregarRepetida(intercambiableMbappe);
    repositorioColecciones.guardar(coleccion);
  }

  // buscarConFiltros

  @Test
  void buscarConFiltros_sinFiltros_retornaTodas() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 1, 10);

    assertEquals(2, resultado.cantidadDeElementos());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    Figurita diMaria = Figurita.builder()
        .id("ARG-11")
        .numero(11)
        .jugador("Di María")
        .seleccion(Seleccion.ARGENTINA)
        .posicion("Extremo")
        .build();
    repositorioFiguritas.guardar(diMaria);
    coleccion.agregarRepetida(new FiguritaIntercambiable(diMaria, 1, List.of(MetodoIntercambio.INTERCAMBIO), "perfil-1"));
    repositorioColecciones.guardar(coleccion);

    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, 11, null, null, null), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals("ARG-11", resultado.contenido().get(0).getFigurita().getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, "ARGENTINA", null, null), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarConFiltros_porJugadorParcialCaseInsensitive_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, "MESSI", null), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
  }

  @Test
  void buscarConFiltros_porTipoIntercambio_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, List.of(MetodoIntercambio.INTERCAMBIO)), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void buscarConFiltros_porTipoSubasta_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, List.of(MetodoIntercambio.SUBASTA)), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.SUBASTA));
  }

  @Test
  void buscarConFiltros_sinResultados_retornaPaginaVacia() {
    var resultado = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, 99, null, null, null), 1, 10);

    assertEquals(0, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }

  @Test
  void buscarConFiltros_conPaginacion_retornaPaginaCorrecta() {
    Figurita diMaria = Figurita.builder()
        .id("ARG-11")
        .numero(11)
        .jugador("Di María")
        .seleccion(Seleccion.ARGENTINA)
        .posicion("Extremo")
        .build();
    repositorioFiguritas.guardar(diMaria);
    coleccion.agregarRepetida(new FiguritaIntercambiable(diMaria, 1, List.of(MetodoIntercambio.INTERCAMBIO), "perfil-1"));
    repositorioColecciones.guardar(coleccion);

    var pagina0 = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 1, 2);
    var pagina1 = repositorioColecciones.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 2, 2);

    assertEquals(3, pagina0.cantidadDeElementos());
    assertEquals(2, pagina0.contenido().size());
    assertEquals(1, pagina1.contenido().size());
  }

  // buscarPorQuery

  @Test
  void buscarPorQuery_porJugador_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("messi", null, 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
  }

  @Test
  void buscarPorQuery_porSeleccion_retornaCoincidencia() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("argentina", null, 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarPorQuery_porNumero_retornaAmbas() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("10", null, 1, 10);

    assertEquals(2, resultado.cantidadDeElementos());
  }

  @Test
  void buscarPorQuery_variosTerminos_aplicaAnd() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("10 argentina", null, 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarPorQuery_conFiltroTipo_aplicaAnd() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("10", List.of(MetodoIntercambio.INTERCAMBIO), 1, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void buscarPorQuery_sinResultados_retornaPaginaVacia() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorQuery("inexistente", null, 1, 10);

    assertEquals(0, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }

  // buscarPorFiguritaIds

  @Test
  void buscarPorFiguritaIds_idsExistentes_retornaLista() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorFiguritaIds(List.of("ARG-10", "FRA-10"));

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarPorFiguritaIds_idsInexistentes_retornaVacio() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorFiguritaIds(List.of("INEXISTENTE"));

    assertTrue(resultado.isEmpty());
  }

  // buscarPorPerfilId

  @Test
  void buscarPorPerfilId_perfilIdExistente_retornaFiguritas() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorPerfilId("perfil-1");

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarPorPerfilId_perfilIdInexistente_retornaVacio() {
    var resultado = repositorioColecciones.buscarIntercambiablesPorPerfilId("perfil-99");

    assertTrue(resultado.isEmpty());
  }
}
