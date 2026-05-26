package app.dto;

import app.model.notificador.Mensaje;
import app.model.notificador.Notificacion;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NotificacionDto {
    private String id;
    private String cuerpo;
    private LocalDateTime fecha;
    private boolean leida;
    private String link;

    public NotificacionDto(Notificacion notificacion) {
        this.id = notificacion.getId();
        this.cuerpo = notificacion.getMensaje().getCuerpo();
        this.fecha = notificacion.getMensaje().getFecha();
        this.leida = notificacion.isLeida();
        this.link = notificacion.getLink();
    }
}
