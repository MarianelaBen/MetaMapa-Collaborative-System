package domain;

import domain.enumerados.EstadoSolicitud;
import lombok.Getter;

public class Solicitud {
  @Getter private Hecho hecho;
  @Getter public String justificacion;
  private EstadoSolicitud estado;

  public Solicitud(Hecho hecho, String justificacion) {
    this.hecho = hecho;
    this.justificacion = justificacion;
    this.estado = EstadoSolicitud.PENDIENTE;
  }

  public void cambiarEstado(EstadoSolicitud nuevoEstado){
    this.estado = nuevoEstado;
  }
}
