package app.servicios;

import app.dto.*;
import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.exceptions.UnauthorizedException;
import app.model.entities.EstadoProceso;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.impl.campos.CamposSubasta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioEstadisticas {

    private final RepositorioPerfiles repositorioPerfiles;
    private final RepositorioPropuestas repositorioPropuestas;
    private final RepositorioSubastas repositorioSubastas;
    private final RepositorioColecciones repositorioColecciones;

    public EstadisticasDto obtenerEstadisticas(SesionDto dto, LocalDate desde, LocalDate hasta) {

        if(!"ADMINISTRADOR".equals(dto.rol())) {
            throw new UnauthorizedException("Solo el admin puede ver las estadisticas");
        }

        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(LocalTime.MAX);

        long totalUsuarios = repositorioPerfiles.contar();

        long totalFiguritasPublicadas = this.repositorioColecciones.contarRepetidas(new ArrayList<>());

        List<Propuesta> propuestasPeriodo =
            repositorioPropuestas.buscarEstadisticasPorRango(desdeDateTime, hastaDateTime);

        int totalPropuestas = propuestasPeriodo.size();

        SubastasFiltro filtros = new SubastasFiltro(0, 20, null, null, "ACTIVA");

        PaginaResultado<Subasta> totalSubastasActivas = repositorioSubastas.buscarTodos(filtros, new CamposSubasta(false, false));

        PropuestasPorEstadoDto propuestasPorEstado = calcularPropuestasPorEstado(propuestasPeriodo);

        FiguritasPorModalidadDto figuritasPorModalidad = calcularFiguritasPorModalidad();

        return new EstadisticasDto(
            totalUsuarios,
            totalFiguritasPublicadas,
            totalPropuestas,
            (int) totalSubastasActivas.cantidadDeElementos(),
            propuestasPorEstado,
            figuritasPorModalidad
        );
    }

    private PropuestasPorEstadoDto calcularPropuestasPorEstado(List<Propuesta> propuestas) {
        Map<EstadoProceso, Long> porEstado = propuestas.stream()
            .collect(Collectors.groupingBy(p -> p.getEstadoActual().getValor(), Collectors.counting()));

        return new PropuestasPorEstadoDto(
            porEstado.getOrDefault(EstadoProceso.PENDIENTE, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.ACEPTADO, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.RECHAZADO, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.CANCELADO, 0L).intValue()
        );
    }

    private FiguritasPorModalidadDto calcularFiguritasPorModalidad() {
        long soloIntercambio = this.repositorioColecciones
            .contarRepetidas(List.of(MetodoIntercambio.INTERCAMBIO));

        long soloSubasta = this.repositorioColecciones
            .contarRepetidas(List.of(MetodoIntercambio.SUBASTA));

        long ambos = this.repositorioColecciones
            .contarRepetidas(List.of(MetodoIntercambio.SUBASTA, MetodoIntercambio.INTERCAMBIO));

        return new FiguritasPorModalidadDto(soloIntercambio, soloSubasta, ambos);

    }
}
