package ar.utn.ba.ddsi.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HechoInputComunDTO {
  private String titulo;
  private String descripcion;
  private String categoria;
  private UbicacionInputDTO ubicacion;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  private String fuente;
  private Boolean fueEliminado;

  // opcionales comunes
  private Set<String> etiquetas;
  private List<String> pathMultimedia;

  // Campos especificos de cada fuente
  private Map<String, Object> extras;
}
