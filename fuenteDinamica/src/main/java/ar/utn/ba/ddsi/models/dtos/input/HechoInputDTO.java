package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Data
@Getter
@Setter
public class HechoInputDTO {

    private String titulo;
    private String descripcion;
    //private Integer idCategoria;
    private CategoriaInputDTO categoria;
    private Ubicacion ciudad;
    private LocalDate fechaAcontecimiento;
}
