package ar.utn.ba.ddsi.models.dtos.output;

import java.time.LocalTime;

public class HoraOutputDTO {
  private LocalTime horaAcontecimiento;
  private Long cantidad;

  public HoraOutputDTO(LocalTime horaAcontecimiento, Long cantidad){
    this.horaAcontecimiento = horaAcontecimiento;
    this.cantidad = cantidad;
  }
}
