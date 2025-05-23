package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.criterios.Criterio;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import lombok.Data;
import lombok.Getter;
import java.util.List;
import java.util.Set;

@Getter
@Data
public class ColeccionDTO {
  //private long handle;
  private String titulo;
  private String descripcion;
  private Fuente fuente;
  private Set<Criterio> criterios;
  private List<HechoDTO> hechosDeLaColeccion;

  public Coleccion toColeccion(){
    Coleccion coleccion =  new Coleccion(
        this.titulo,
        this.descripcion,
        this.fuente
    );
    List<Hecho> hechos = hechosDeLaColeccion.stream()
        .map(HechoDTO::toHecho)
        .toList();
    coleccion.setHechosDeLaColeccion(hechos);
    return coleccion;
    //agrego que cuando se instancia una nueva coleccion tambien se instancien sus hechos
  }
}

