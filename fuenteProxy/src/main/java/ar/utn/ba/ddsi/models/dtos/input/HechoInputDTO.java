package ar.utn.ba.ddsi.models.dtos.input;

import lombok.Data;
import lombok.Getter;
import java.time.LocalDate;


@Getter
@Data
public class HechoInputDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;


}



