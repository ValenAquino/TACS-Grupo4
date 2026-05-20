package app.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EstadisticasDto {
    private long totalUsuarios;
    private int totalFiguritasPublicadas;
    private int totalPropuestas;
    private int totalSubastasActivas;
    private PropuestasPorEstadoDto propuestasPorEstado;
    private FiguritasPorModalidadDto figuritasPorModalidad;
    private List<SeleccionCantidadDto> topSelecciones;
}
