package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Solicitud {
  private Long id;
  private TipoSolicitud tipoSolicitud;
  private Hecho hecho;
  private EstadoSolicitud estado;
  private LocalDate fechaSolicitud;
  private LocalDate fechaAtencion;
  private String comentario;
  private Long idAdministradorQueAtendio;

  public Solicitud(Hecho hecho, TipoSolicitud tipo) {
    this.hecho = hecho;
    this.comentario = null;
    this.estado = EstadoSolicitud.PENDIENTE;
    this.idAdministradorQueAtendio = null;
    this.fechaSolicitud = LocalDate.now();
    this.fechaAtencion = null;
    this.estado = EstadoSolicitud.PENDIENTE;
    this.tipoSolicitud = tipo;
  }

  public void cambiarEstado(EstadoSolicitud nuevoEstado){
    this.estado = nuevoEstado;
  }
}