package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Data
public class HechoOutputDTO {
  @NotNull
  private String titulo;
  @NotNull
  private String descripcion;
  @NotNull
  private Long idCategoria;
  @NotNull
  private Ubicacion ubicacion;
  @NotNull
  private LocalDate fechaAcontecimiento;
  @NotNull
  private LocalDate fechaCarga;
  @NotNull
  private Origen origen;
  // private boolean fueEliminado;    no es un dato que se pueda enviar
  private Set<Long> idEtiquetas;
  @NotNull
  private Long idContribuyente;

  private Long idContenidoMultimedia;
}

/*
Dependencia para validacion de datos obligatorios
- Se usa agregando @NotBlank a los campos obligatorios
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
 */