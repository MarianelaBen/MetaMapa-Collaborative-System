package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Contribuyente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HechoOutputDTO {
    private Integer id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private Set<String> idEtiquetas;
  private List<String> idContenidoMultimedia;
  private String fuenteExterna;
  private Contribuyente contribuyente;
}