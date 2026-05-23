package app.dto.filtros;

public record SubastasFiltro(
  Integer pagina,
  Integer limite,
  String autorId,
  String participanteId,
  String estado
) {
}
