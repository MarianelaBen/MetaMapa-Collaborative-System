package ar.utn.ba.ddsi.Metamapa.dtos.input;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class ColeccionInputDTO {
    private String titulo;
    private String descripcion;
    private String handle;
    private String algoritmoDeConsenso;
    private Set<Long> fuenteIds;
    private Set<Long> criterioIds;
    private Set<Long> hechoIds;
}
