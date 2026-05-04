package app.servicios;

import app.dto.SubastaDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ISubastaService {

    /**
     * Crea una subasta para la figurita indicada y notifica a los usuarios
     * que la tienen en su lista de faltantes.
     */
    SubastaDto crearSubasta(String userId, LocalDateTime fechaInicio, LocalDateTime fechaFin,
                            String figuritaSubastadaId);

    /**
     * Registra una oferta en la subasta. Valida que no haya figuritas ofrecidas duplicadas
     * y crea una propuesta asociada a la subasta.
     */
    SubastaDto ofertarEnSubasta(String userId, String usuarioDestinoId,
                                String subastaId, List<String> rawFiguritasId);

    /**
     * Marca la oferta indicada como SELECCIONADO. Si había otra en SELECCIONADO,
     * la pasa a PENDIENTE.
     */
    SubastaDto seleccionarOferta(String subastaId, String ofertaId);

    /**
     * Marca la oferta indicada como RECHAZADO.
     */
    SubastaDto rechazarOferta(String subastaId, String ofertaId);

    /**
     * Cancela la subasta: setea fechaCierre a now y todas las ofertas a RECHAZADO.
     */
    SubastaDto cancelarSubasta(String subastaId);

    /**
     * Cierra la subasta: setea fechaCierre a now, la oferta seleccionada a ACEPTADO
     * y el resto a RECHAZADO.
     */
    SubastaDto cerrarSubasta(String subastaId);
}