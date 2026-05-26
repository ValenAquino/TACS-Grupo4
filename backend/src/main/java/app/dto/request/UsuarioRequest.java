package app.dto.request;

import app.model.entities.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UsuarioRequest {
  @NotBlank
  String nombre;
  @NotBlank
  String contrasenia;
  @NotNull
  Rol rol;
}
