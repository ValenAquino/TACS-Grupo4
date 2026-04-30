package app.servicios;

import app.dto.SubastaDto;
import app.model.entities.Figurita;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;

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
}
