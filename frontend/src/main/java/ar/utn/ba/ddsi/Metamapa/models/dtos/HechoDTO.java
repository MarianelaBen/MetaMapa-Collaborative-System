package ar.utn.ba.ddsi.Metamapa.models.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private Set<Long> idEtiquetas;
  private List<String> idContenidoMultimedia;
  private String fuenteExterna;


  public HechoDTO(String titulo, String descripcion, String categoria, LocalDateTime fechaAcontecimiento, String provincia, Long id) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.provincia = provincia;
    this.id = id;
  }
}
