package app.dto;

import app.model.entities.Figurita;
import app.model.entities.Sugerencia;
import lombok.Getter;
import java.util.List;

@Getter
public class SugerenciaDto {
  PerfilDto usuario;
  List<Figurita> figuritas;

  public SugerenciaDto(Sugerencia sugerencia) {
    this.usuario = new PerfilDto(sugerencia.getUsuarioSugerido());
    this.figuritas = sugerencia.getFiguritasSugeridas();
  }
}
