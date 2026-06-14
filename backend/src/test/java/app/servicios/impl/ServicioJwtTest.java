package app.servicios.impl;

import app.dto.SesionDto;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import app.model.entities.Coleccion;
import app.model.entities.Rol;
import app.servicios.ServicioJwt;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioJwtTest {

  private ServicioJwt servicioJwt;

  private Usuario usuario;
  private Perfil perfil;
  private Coleccion coleccion;

  @BeforeEach
  void setUp() {
    servicioJwt = new ServicioJwt(
        "12345678901234567890123456789012",
        Duration.ofHours(12)
    );

    usuario = mock(Usuario.class);
    perfil = mock(Perfil.class);
    coleccion = mock(Coleccion.class);

    when(usuario.getId()).thenReturn("user-1");
    when(usuario.getRol()).thenReturn(Rol.ADMINISTRADOR);


    when(perfil.getId()).thenReturn("perfil-1");
    when(perfil.getColeccion()).thenReturn(coleccion);

    when(coleccion.getId()).thenReturn("col-1");
  }

  @Test
  void generarToken_y_validarToken_devuelveClaimsCorrectos() {
    String token = servicioJwt.generarToken(usuario, perfil);

    Claims claims = servicioJwt.validarToken(token);

    assertEquals("user-1", claims.get("usuarioId", String.class));
    assertEquals("ADMINISTRADOR", claims.get("rol", String.class));
    assertEquals("perfil-1", claims.get("perfilId", String.class));
    assertEquals("col-1", claims.get("colId", String.class));
    assertEquals("user-1", claims.getSubject());
  }

  @Test
  void obtenerSesion_devuelveSesionDtoCorrecto() {
    String token = servicioJwt.generarToken(usuario, perfil);

    SesionDto sesion = servicioJwt.obtenerSesion(token);

    assertEquals("user-1", sesion.usuarioId());
    assertEquals("ADMINISTRADOR", sesion.rol());
    assertEquals("perfil-1", sesion.perfilId());
    assertEquals("col-1", sesion.colId());
  }

  @Test
  void getColeccionId_devuelveColIdCorrecto() {
    String token = servicioJwt.generarToken(usuario, perfil);

    String colId = servicioJwt.getColeccionId(token);

    assertEquals("col-1", colId);
  }

  @Test
  void getPerfilId_devuelvePerfilIdCorrecto() {
    String token = servicioJwt.generarToken(usuario, perfil);

    String perfilId = servicioJwt.getPerfilId(token);

    assertEquals("perfil-1", perfilId);
  }

  @Test
  void validarToken_conTokenInvalido_lanzaExcepcion() {
    String tokenInvalido = "token.que.no.es.valido";

    assertThrows(Exception.class, () -> {
      servicioJwt.validarToken(tokenInvalido);
    });
  }
}
