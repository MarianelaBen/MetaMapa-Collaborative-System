package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class SolicitudDeEliminacion {
   private Long id;
   private Hecho hecho;
   private String justificacion;
   private EstadoSolicitud estado;
    //@Getter @Setter private Administrador administradorQueAtendio;
    //El agregador atiende las solicitudes de forma automática, por lo tanto, no debería tener un Administrador.
   private LocalDateTime fechaEntrada;
   private LocalDateTime fechaAtencion;

  public SolicitudDeEliminacion(Hecho hecho, String justificacion) {
    this.hecho = hecho;
    this.justificacion = justificacion;
    this.estado = EstadoSolicitud.PENDIENTE;
    //this.administradorQueAtendio = null;
    this.fechaEntrada = null;
    this.fechaAtencion = null;
  }

  public void cambiarEstado(EstadoSolicitud nuevoEstado){
    this.estado = nuevoEstado;
  }
}
