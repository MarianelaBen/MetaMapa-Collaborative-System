package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Categoria;
import lombok.Data;
import java.time.LocalDate;


@Data
public class HechoDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
}

