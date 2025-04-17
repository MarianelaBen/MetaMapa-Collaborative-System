package domain.fuentes;
import java.util.Set;
import domain.Hecho;
import lombok.Getter;

public abstract class Fuente {
    public abstract Set<Hecho> leerHechos();
}
