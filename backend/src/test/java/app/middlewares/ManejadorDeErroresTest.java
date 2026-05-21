package app.middlewares;

import app.dto.response.ErrorResponse;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ManejadorDeErroresTest {

  private final ErrorHandler errorHandler = new ErrorHandler();

  // -------------------------
  // NOT FOUND
  // -------------------------
  @Test
  void handleNotFound_devuelve404() {
    NotFoundException ex = new NotFoundException("recurso no encontrado");

    ResponseEntity<ErrorResponse> response =
        errorHandler.handleNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    ErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(404, body.status());
    assertEquals("recurso no encontrado", body.message());
    assertEquals(Map.of(), body.errors());
    assertNotNull(body.timestamp());
  }

  // -------------------------
  // BAD REQUEST
  // -------------------------
  @Test
  void handleBadRequest_devuelve400() {
    BadRequestException ex = new BadRequestException("request inválida");

    ResponseEntity<ErrorResponse> response =
        errorHandler.handleBadRequest(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    ErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(400, body.status());
    assertEquals("request inválida", body.message());
    assertEquals(Map.of(), body.errors());
    assertNotNull(body.timestamp());
  }

  // -------------------------
  // UNAUTHORIZED
  // -------------------------
  @Test
  void handleUnauthorized_devuelve401() {
    UnauthorizedException ex = new UnauthorizedException("no autorizado");

    ResponseEntity<ErrorResponse> response =
        errorHandler.handleUnathorized(ex);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    ErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(401, body.status());
    assertEquals("no autorizado", body.message());
    assertEquals(Map.of(), body.errors());
    assertNotNull(body.timestamp());
  }

  // -------------------------
  // GENERIC EXCEPTION (500)
  // -------------------------
  @Test
  void handleException_devuelve500() {
    Exception ex = new RuntimeException("boom");

    ResponseEntity<ErrorResponse> response =
        errorHandler.handleInternalServerError(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    ErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(500, body.status());
    assertEquals("Ocurrió un error interno del servidor", body.message());
    assertEquals(Map.of(), body.errors());
    assertNotNull(body.timestamp());
  }
}
