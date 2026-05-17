package app.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Repetidas<T> {
  private int publicadas;
  private int disponibles;
  @JsonUnwrapped
  private PaginaResultado<T> data;
}
