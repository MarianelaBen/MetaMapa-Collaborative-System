// src/main/java/ar/utn/ba/ddsi/models/dtos/output/ColeccionOutputDTO.java
package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ColeccionOutputDTO {
  private String titulo;
  private String descripcion;
  private String handle;
  private String algoritmoDeConsenso;
  private Set<Long> fuenteIds;
  private Set<Long> criterioIds;
  private Set<Long> hechoIds;

  public static ColeccionOutputDTO fromEntity(Coleccion c) {
    ColeccionOutputDTO dto = new ColeccionOutputDTO();
    dto.setTitulo(c.getTitulo());
    dto.setDescripcion(c.getDescripcion());
    dto.setHandle(c.getHandle());
    dto.setAlgoritmoDeConsenso(c.getAlgoritmoDeConsenso().name());
    // Extraemos solo los IDs de las colecciones relacionadas
    dto.setFuenteIds(
        c.getFuentes().stream()
            .map(f -> f.getId())
            .collect(Collectors.toSet())
    );
    dto.setHechoIds(
        c.getHechos().stream()
            .map(h -> h.getId())
            .collect(Collectors.toSet())
    );
    return dto;
  }
}
