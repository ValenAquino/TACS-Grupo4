package app.dto;

import java.util.List;

public record PaginaResultado<T>(
    List<T> contenido,
    long cantidadDeElementos,
    int cantidadDePaginas,
    int numero
) {
}
