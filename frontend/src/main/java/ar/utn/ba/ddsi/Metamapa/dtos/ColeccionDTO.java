package ar.utn.ba.ddsi.Metamapa.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ColeccionDTO {
  private String titulo;
  private String descripcion;
  private List<HechoDTO> hechos = new ArrayList<>();
  private String handle;
  private String algoritmoDeConsenso;
  private Set<Long> fuenteIds;
  private Set<Long> criterioIds;


  public ColeccionDTO(String titulo, String descripcion, String handle, List<HechoDTO> hechos) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.handle = handle;
    this.hechos = hechos;
  }
}
