package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "solicitud_eliminacion")
public class SolicitudDeEliminacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @ManyToOne
  @JoinColumn(name = "hecho_id")
  private Hecho hecho;
  @Column(name = "justificacion")
  private String justificacion;
  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private EstadoSolicitud estado;
  @Column(name = "fecha_entrada")
  private LocalDateTime fechaEntrada;
  @Column(name = "fecha_atencion")
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
