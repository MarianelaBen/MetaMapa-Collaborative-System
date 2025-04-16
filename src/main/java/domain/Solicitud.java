package domain;

import lombok.Getter;

public class Solicitud {
  @Getter private Hecho hecho;
  @Getter private String justificacion;
  //private EstadoSolicitud estado;

  public Solicitud(Hecho hecho, String justificacion) {
    this.hecho = hecho;
    this.justificacion = justificacion;
    //this.estado = EstadoSolicitud.PENDIENTE
  }
}
