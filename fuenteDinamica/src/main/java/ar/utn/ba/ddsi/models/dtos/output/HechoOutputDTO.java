package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;

@Data
public class HechoOutputDTO {
  @NotNull
  private String titulo;
  @NotNull
  private String descripcion;
  @NotNull
  private String nombreCategoria;
  @NotNull
  private UbicacionOutputDTO ubicacion;
  @NotNull
  private LocalDate fechaAcontecimiento;
  @NotNull
  private LocalDate fechaCarga;
  @NotNull
  private Origen origen;
  @NotNull
  private boolean fueEliminado;
  @NotNull
  private Set<String> nombreEtiquetas;
  @NotNull
  private ContribuyenteOutputDTO contribuyente;

  private List<String> pathContenidoMultimedia;
}

/*
Dependencia para validacion de datos obligatorios
- Se usa agregando @NotBlank a los campos obligatorios
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
 */