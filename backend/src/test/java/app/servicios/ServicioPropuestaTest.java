package app.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import app.MongoTestBase;
import app.dto.PropuestaDto;
import app.dto.request.CrearPropuestaRequest;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.EstadoProceso;
import app.model.entities.Figurita;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import java.util.List;
import app.servicios.ServicioPropuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicioPropuestaTest  extends MongoTestBase {


  @Autowired
  ServicioPropuesta propuestaService;

  private Perfil lucas;
  private Perfil sofia;
  private Figurita messi;
  private Figurita mbappe;

  @BeforeEach
  void setUp() {
    Usuario user = new Usuario("u-1000", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);

    Coleccion colec = new Coleccion("c-1000");
    lucas = Perfil.builder()
        .id("1000").usuario(user).nombre("Lucas")
        .coleccion(colec)
        .build();
    repositorioColecciones.guardar(colec);
    repositorioPerfiles.guardar(lucas);

    user = new Usuario("u-1001", Rol.USUARIO, "Sofía", "fiscella");
    repositorioUsuarios.guardar(user);

    colec = new Coleccion("c-1001");
    sofia = Perfil.builder()
        .id("1001").usuario(user).nombre("Sofía")
        .coleccion(colec)
        .build();

    repositorioColecciones.guardar(colec);
    repositorioPerfiles.guardar(sofia);

    messi  = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    mbappe = new Figurita("FRA-10", 10, "Mbappé", Seleccion.FRANCIA, null);
    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(mbappe);
  }

  @Test
  void crearPropuestaDevuelveDto() {
    CrearPropuestaRequest request = new CrearPropuestaRequest(
        "1000", "1001", "ARG-10", List.of("FRA-10"));

    PropuestaDto resultado = propuestaService.crearPropuesta(request);

    assertEquals("1000", resultado.getAutor().getId());
    assertEquals("1001", resultado.getDestinatario().getId());
    assertEquals("ARG-10", resultado.getFiguritaBuscada().getId());
    assertEquals(EstadoProceso.PENDIENTE, resultado.getEstado());
  }

  @Test
  void crearPropuestaUsuarioOrigenNoExisteLanzaNotFoundException() {
    CrearPropuestaRequest request = new CrearPropuestaRequest(
        "9999", "1001", "ARG-10", List.of("FRA-10"));


    assertThrows(NotFoundException.class,
        () -> propuestaService.crearPropuesta(request));
  }

  @Test
  void crearPropuestaUsuarioDestinoNoExisteLanzaNotFoundException() {
    CrearPropuestaRequest request = new CrearPropuestaRequest(
        "1000", "9999", "ARG-10", List.of("FRA-10"));

    assertThrows(NotFoundException.class,
        () -> propuestaService.crearPropuesta(request));
  }
}