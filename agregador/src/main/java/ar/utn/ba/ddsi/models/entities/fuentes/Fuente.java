package ar.utn.ba.ddsi.models.entities.fuentes;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public abstract class Fuente {
    public abstract void leerHechos();
    public abstract List<Hecho> getHechos();
    public abstract TipoFuente getTipo();
}
