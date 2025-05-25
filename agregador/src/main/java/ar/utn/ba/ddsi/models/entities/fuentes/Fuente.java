package ar.utn.ba.ddsi.models.entities.fuentes;
import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public abstract class Fuente {
    public abstract List<HechoDTO> getHechos();
    public abstract TipoFuente getTipo();
}
