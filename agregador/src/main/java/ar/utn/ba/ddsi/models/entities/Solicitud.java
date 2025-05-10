package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


public class Solicitud {
  @Getter private Hecho hecho;
  @Getter private String justificacion;
  @Getter private EstadoSolicitud estado;
  //@Getter @Setter private Administrador administradorQueAtendio;
  //El agregador atiende las solicitudes de forma automática, por lo tanto, no debería tener un Administrador.
  @Getter @Setter private LocalDateTime fechaEntrada;
  @Getter @Setter private LocalDateTime fechaAtencion;

  public Solicitud(Hecho hecho, String justificacion) {
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
