package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import java.util.List;

public interface UsuarioService {
    OperacionesDto getOperacionesUsuario(String userId);
    List<FiguritaIntercambiableDto> getIntercambiablesUsuario(String userId);
}
