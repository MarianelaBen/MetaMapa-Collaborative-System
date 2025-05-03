package ar.utn.ba.ddsi.MetaMapa.models.entities.fuentes;
import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;
import java.util.List;

public abstract class Fuente {
    public abstract void leerHechos();
    public abstract List<Hecho> getHechos();
}
