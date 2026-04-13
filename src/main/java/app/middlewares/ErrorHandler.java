package app.middlewares;

import app.exceptions.FiguritaDuplicadaException;
import app.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(404).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleBadRequest(Exception ex) {
    return ResponseEntity.status(400).body(ex.getMessage());
  }

  @ExceptionHandler(FiguritaDuplicadaException.class)
  public ResponseEntity<String> handleFiguritaDuplicada(Exception ex) {
    return ResponseEntity.status(400).body(ex.getMessage());
  }
}
