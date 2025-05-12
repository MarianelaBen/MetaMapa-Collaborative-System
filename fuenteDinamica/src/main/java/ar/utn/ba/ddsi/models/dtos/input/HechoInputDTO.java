package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class HechoInputDTO {

    private String titulo;
    private String descripcion;
    private Integer idCategoria;
    private Ubicacion ciudad;     //TODO tenemos que ver si sera mas especifico
    private LocalDate fechaAcontecimiento;
}
