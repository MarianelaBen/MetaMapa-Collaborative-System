package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudInputDTO {
  private String justificacion;

  // getters & setters

  public SolicitudDeEliminacion toEntity() {
    SolicitudDeEliminacion s = new SolicitudDeEliminacion();
    s.setJustificacion(this.justificacion);
    return s;
  }
}