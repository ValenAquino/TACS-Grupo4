package app.servicios;

import app.dto.EstadisticasDto;
import app.dto.FiguritasPorModalidadDto;
import app.dto.PropuestasPorEstadoDto;
import app.dto.SeleccionCantidadDto;
import app.dto.filtros.SubastasFiltro;
import app.model.entities.EstadoProceso;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Subasta;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioEstadisticas {

    private final RepositorioPerfiles repositorioPerfiles;
    private final RepositorioPropuestas repositorioPropuestas;
    private final RepositorioSubastas repositorioSubastas;

    public EstadisticasDto obtenerEstadisticas() {
        long totalUsuarios = repositorioPerfiles.contar();

        List<FiguritaIntercambiable> todasLasRepetidas = repositorioPerfiles.buscarTodos().stream()
            .flatMap(u -> u.getColeccion().getRepetidas().stream())
            .collect(Collectors.toList());

        int totalFiguritasPublicadas = todasLasRepetidas.size();

        int totalPropuestas = repositorioPropuestas.contar();

        int totalSubastasActivas = repositorioSubastas.contar();

        PropuestasPorEstadoDto propuestasPorEstado = calcularPropuestasPorEstado();
        FiguritasPorModalidadDto figuritasPorModalidad = calcularFiguritasPorModalidad(todasLasRepetidas);
        List<SeleccionCantidadDto> topSelecciones = calcularTopSelecciones(todasLasRepetidas);

        return new EstadisticasDto(
            totalUsuarios,
            totalFiguritasPublicadas,
            totalPropuestas,
            totalSubastasActivas,
            propuestasPorEstado,
            figuritasPorModalidad,
            topSelecciones
        );
    }

    private PropuestasPorEstadoDto calcularPropuestasPorEstado() {
        Map<EstadoProceso, Long> porEstado = repositorioPropuestas.buscarTodos().stream()
            .collect(Collectors.groupingBy(p -> p.obtenerEstadoActual().getValor(), Collectors.counting()));

        return new PropuestasPorEstadoDto(
            porEstado.getOrDefault(EstadoProceso.PENDIENTE, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.ACEPTADO, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.RECHAZADO, 0L).intValue()
        );
    }

    private FiguritasPorModalidadDto calcularFiguritasPorModalidad(List<FiguritaIntercambiable> repetidas) {
        int soloIntercambio = (int) repetidas.stream()
            .filter(f -> f.soporta(MetodoIntercambio.INTERCAMBIO) && !f.soporta(MetodoIntercambio.SUBASTA))
            .count();
        int soloSubasta = (int) repetidas.stream()
            .filter(f -> f.soporta(MetodoIntercambio.SUBASTA) && !f.soporta(MetodoIntercambio.INTERCAMBIO))
            .count();
        int ambos = (int) repetidas.stream()
            .filter(f -> f.soporta(MetodoIntercambio.INTERCAMBIO) && f.soporta(MetodoIntercambio.SUBASTA))
            .count();
        return new FiguritasPorModalidadDto(soloIntercambio, soloSubasta, ambos);
    }

    private List<SeleccionCantidadDto> calcularTopSelecciones(List<FiguritaIntercambiable> repetidas) {
        return repetidas.stream()
            .collect(Collectors.groupingBy(f -> f.getFigurita().getSeleccion().name(), Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .map(e -> new SeleccionCantidadDto(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
}
