package app.exceptions;

public class UsuarioException extends BadRequestException {
  public UsuarioException(String message) {
    super(message);
  }
}
