package app.servicios;

import app.dto.EstadisticasDto;
import app.dto.FiguritasPorModalidadDto;
import app.dto.PropuestasPorEstadoDto;
import app.dto.SeleccionCantidadDto;
import app.dto.filtros.RepetidasFiltro;
import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.EstadoProceso;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Subasta;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.impl.campos.CamposPerfil;
import app.repositories.impl.campos.CamposSubasta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final RepositorioColecciones repositorioColecciones;

    public EstadisticasDto obtenerEstadisticas() {
        long totalUsuarios = repositorioPerfiles.contar();

        long totalFiguritasPublicadas = this.repositorioColecciones.contarRepetidas(new ArrayList<>());

        int totalPropuestas = repositorioPropuestas.contar();

        SubastasFiltro filtros = new SubastasFiltro(0, 20, null, null, "ACTIVA");

        PaginaResultado<Subasta> totalSubastasActivas = repositorioSubastas.buscarTodos(filtros, new CamposSubasta(false, false));

        PropuestasPorEstadoDto propuestasPorEstado = calcularPropuestasPorEstado();

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

    private PropuestasPorEstadoDto calcularPropuestasPorEstado() {
        Map<EstadoProceso, Long> porEstado = repositorioPropuestas.buscarTodosEstadisticas().stream()
            .collect(Collectors.groupingBy(p -> p.getEstadoActual().getValor(), Collectors.counting()));

        return new PropuestasPorEstadoDto(
            porEstado.getOrDefault(EstadoProceso.PENDIENTE, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.ACEPTADO, 0L).intValue(),
            porEstado.getOrDefault(EstadoProceso.RECHAZADO, 0L).intValue()
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
