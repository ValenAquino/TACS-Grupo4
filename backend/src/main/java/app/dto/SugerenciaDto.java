package app.dto;

import app.model.entities.Figurita;
import app.model.entities.Sugerencia;
import lombok.Getter;
import java.util.List;

@Getter
public class SugerenciaDto {
  PerfilDto perfil;
  List<Figurita> figuritasRecomendadas;
  List<Figurita> figuritasNecesarias;

  public SugerenciaDto(Sugerencia sugerencia) {
    this.perfil = new PerfilDto(sugerencia.getDestinatario());
    this.figuritasRecomendadas = sugerencia.getFiguritasSugeridas();
    this.figuritasNecesarias = sugerencia.getFiguritasNecesarias();
  }
}
