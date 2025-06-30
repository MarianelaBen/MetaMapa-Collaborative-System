package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class ColeccionInputDTO {
  private String titulo;
  private String descripcion;
  private String handle;
  private TipoAlgoritmoDeConsenso algoritmoDeConsenso;
  private Set<Long> fuenteIds;
  private Set<Long> criterioIds;
  private Set<Long> hechoIds;


  public Coleccion toEntity() {
    Coleccion c = new Coleccion();
    c.setTitulo(this.titulo);
    c.setDescripcion(this.descripcion);
    c.setHandle(this.handle);
    c.setAlgoritmoDeConsenso(this.algoritmoDeConsenso);
    // Las asociaciones a entidades compuestas se resuelven en el Service
    return c;
  }
}
