package app.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.client.dto.TheSportsDbPlayerDto;
import app.client.dto.TheSportsDbResponse;
import app.exceptions.RateLimitException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class TheSportsDbImagenProveedorTest {

  private static final String BASE_URL = "https://www.thesportsdb.com/api/v1/json";
  private static final String API_KEY = "123";

  private RestTemplate restTemplate;
  private TheSportsDbImagenProveedor proveedor;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    proveedor = new TheSportsDbImagenProveedor(restTemplate, BASE_URL, API_KEY);
  }

  @Test
  void buscarImagen_jugadorEncontrado_retornaUrl() {
    TheSportsDbPlayerDto jugador = new TheSportsDbPlayerDto();
    jugador.setStrThumb("https://cdn.thesportsdb.com/images/media/player/thumb/messi.jpg");
    TheSportsDbResponse response = new TheSportsDbResponse();
    response.setPlayer(List.of(jugador));
    when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

    Optional<String> resultado = proveedor.buscarImagen("Messi");

    assertTrue(resultado.isPresent());
    assertEquals("https://cdn.thesportsdb.com/images/media/player/thumb/messi.jpg", resultado.get());
  }

  @Test
  void buscarImagen_jugadorNoEncontrado_listaVacia_retornaEmpty() {
    TheSportsDbResponse response = new TheSportsDbResponse();
    response.setPlayer(Collections.emptyList());
    when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

    assertFalse(proveedor.buscarImagen("JugadorInexistente").isPresent());
  }

  @Test
  void buscarImagen_responseNull_retornaEmpty() {
    when(restTemplate.getForObject(anyString(), any())).thenReturn(null);

    assertFalse(proveedor.buscarImagen("Messi").isPresent());
  }

  @Test
  void buscarImagen_playerNull_retornaEmpty() {
    TheSportsDbResponse response = new TheSportsDbResponse();
    response.setPlayer(null);
    when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

    assertFalse(proveedor.buscarImagen("Messi").isPresent());
  }

  @Test
  void buscarImagen_http429_lanzaRateLimitException() {
    when(restTemplate.getForObject(anyString(), any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

    assertThrows(RateLimitException.class, () -> proveedor.buscarImagen("Messi"));
  }

  @Test
  void buscarImagen_errorHttpDistintoDeRateLimit_retornaEmpty() {
    when(restTemplate.getForObject(anyString(), any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    assertFalse(proveedor.buscarImagen("Messi").isPresent());
  }

  @Test
  void buscarImagen_strThumbBlanco_retornaEmpty() {
    TheSportsDbPlayerDto jugador = new TheSportsDbPlayerDto();
    jugador.setStrThumb("   ");
    TheSportsDbResponse response = new TheSportsDbResponse();
    response.setPlayer(List.of(jugador));
    when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

    assertFalse(proveedor.buscarImagen("Messi").isPresent());
  }

  @Test
  void buscarImagen_excepcionGenerica_retornaEmpty() {
    when(restTemplate.getForObject(anyString(), any()))
        .thenThrow(new RuntimeException("error de red inesperado"));

    assertFalse(proveedor.buscarImagen("Messi").isPresent());
  }

  @Test
  void buscarImagen_nombreConTilde_normalizaCorrectamente() {
    TheSportsDbResponse response = new TheSportsDbResponse();
    response.setPlayer(Collections.emptyList());
    org.mockito.ArgumentCaptor<String> urlCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
    when(restTemplate.getForObject(urlCaptor.capture(), any())).thenReturn(response);

    proveedor.buscarImagen("Di María");

    assertTrue(urlCaptor.getValue().contains("di_maria"),
        "La URL debería contener 'di_maria' pero fue: " + urlCaptor.getValue());
  }
}
