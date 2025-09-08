package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HechoOutputDTO {
  @NotNull
  private String titulo;
  @NotNull
  private String descripcion;
  @NotNull
  private String categoria;
  @NotNull
  private UbicacionOutputDTO ubicacion;
  @NotNull
  private LocalDate fechaAcontecimiento;
  @NotNull
  private LocalDate fechaCarga;
  @NotNull
  private boolean fueEliminado;
  @NotNull
  private Set<String> etiquetas;

  private JsonNode particulares;

  private List<String> pathContenidoMultimedia;
}