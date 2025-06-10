package ar.utn.ba.ddsi.models.dtos.output;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ContribuyenteOutputDTO {
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;
}
