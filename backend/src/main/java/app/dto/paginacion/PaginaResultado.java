package app.dto.paginacion;

import java.util.List;
import java.util.function.Function;

public record PaginaResultado<T>(
    List<T> contenido,
    long cantidadDeElementos,
    int cantidadDePaginas,
    int numero
) {

  public <C> PaginaResultado<C> mapearA(Function<T, C> mapper) {
    return new PaginaResultado<>(
        contenido.stream()
            .map(mapper)
            .toList(),
        cantidadDeElementos,
        cantidadDePaginas,
        numero
    );
  }
}
