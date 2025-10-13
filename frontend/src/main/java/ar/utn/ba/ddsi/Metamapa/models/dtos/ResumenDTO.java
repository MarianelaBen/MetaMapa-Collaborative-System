package ar.utn.ba.ddsi.Metamapa.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenDTO {
  private long totalHechos;
  private long totalFuentes;
  private long solicitudesPendientes;
  //private long usuariosActivos; TODO estaria bueno pero el tiempo es tirano
}
