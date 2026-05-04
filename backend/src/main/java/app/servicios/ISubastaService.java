package app.servicios;

import app.dto.subasta.SubastaParticipoDto;
import app.dto.subasta.MisSubastasResponseDto;
import app.dto.subasta.SubastaDto;
import app.dto.subasta.SubastasParticipoResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ISubastaService {

    /**
     * Crea una subasta para la figurita indicada y notifica a los usuarios
     * que la tienen en su lista de faltantes.
     */
    void crearSubasta(String userId, String figuritaId, Integer duracionEnHoras,
                      List<String> figuritasDeseadasIds, Integer calificacionMinima);

    /**
     * Registra una oferta en la subasta. Valida que no haya figuritas ofrecidas duplicadas
     * y crea una propuesta asociada a la subasta.
     */
    void ofertarEnSubasta(String userId, String usuarioDestinoId,
                                String subastaId, List<String> rawFiguritasId);

    /**
     * Marca la oferta indicada como SELECCIONADO. Si había otra en SELECCIONADO,
     * la pasa a PENDIENTE.
     */
    void seleccionarOferta(String subastaId, String ofertaId);

    /**
     * Marca la oferta indicada como RECHAZADO.
     */
    void rechazarOferta(String subastaId, String ofertaId);

    /**
     * Cancela la subasta: setea fechaCierre a now y todas las ofertas a RECHAZADO.
     */
    void cancelarSubasta(String subastaId);

    /**
     * Cierra la subasta: setea fechaCierre a now, la oferta seleccionada a ACEPTADO
     * y el resto a RECHAZADO.
     */
    void cerrarSubasta(String subastaId);

    /**
     * Obtiene las subastas donde el usuario logueado es el autor.
     */
    MisSubastasResponseDto obtenerMisSubastas(String userId);

    /**
     * Obtiene las subastas donde el usuario logueado tiene al menos una oferta.
     */
    SubastasParticipoResponseDto obtenerSubastasParticipo(String userId);
}