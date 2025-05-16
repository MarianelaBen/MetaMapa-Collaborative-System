package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Etiqueta;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

//TODO no se si va en imput o ouput

@Data
public class HechoDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Ubicacion ubicacion; //separar entre longitud latitud
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  //TODO completar y modificar
}

