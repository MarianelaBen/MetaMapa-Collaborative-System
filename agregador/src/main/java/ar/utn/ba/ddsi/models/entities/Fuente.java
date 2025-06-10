package ar.utn.ba.ddsi.models.entities;
import ar.utn.ba.ddsi.adapters.IFuenteProxyAdapter;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Fuente {
    public abstract List<Hecho> getHechos();
    public abstract TipoFuente getTipo();
}

