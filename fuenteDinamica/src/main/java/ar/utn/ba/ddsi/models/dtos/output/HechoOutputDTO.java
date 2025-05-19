package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Data
public class HechoOutputDTO {
  private String titulo;
  private String descripcion;
  private Long idCategoria;
  private Ubicacion ubicacion;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  private Origen origen;
  // private boolean fueEliminado;    no es un dato que se pueda enviar
  private Set<Long> idEtiquetas;
}

/*
Dependencia para validacion de datos obligatorios
- Se usa agregando @NotBlank a los campos obligatorios
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
 */