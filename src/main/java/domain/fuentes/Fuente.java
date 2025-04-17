package domain.fuentes;
import java.util.Set;
import domain.Hecho;

public abstract class Fuente {
    public abstract Set<Hecho> leerHechos();
}
