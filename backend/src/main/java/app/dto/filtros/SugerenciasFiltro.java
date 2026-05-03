package app.dto.filtros;
import app.model.entities.Sugerencia;
import java.util.Objects;

public record SugerenciasFiltro (String tipo) {

  public boolean verifica(Sugerencia sugerencia) {
    if(Objects.equals(this.tipo, "1a1")) {
      return sugerencia.getFiguritasNecesarias().size() == 1 && sugerencia.getFiguritasSugeridas().size() == 1;
    } else if (Objects.equals(this.tipo, "Na1")) {
      return sugerencia.getFiguritasNecesarias().size() > 1 && sugerencia.getFiguritasSugeridas().size() == 1;
    } else if(Objects.equals(this.tipo, "1aN")){
      return sugerencia.getFiguritasNecesarias().size() == 1 && sugerencia.getFiguritasSugeridas().size() > 1;
    } else return true;
  }
}
