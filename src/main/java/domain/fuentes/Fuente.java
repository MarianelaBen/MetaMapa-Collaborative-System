package domain.fuentes;
import java.util.Set;
import domain.Hecho;
import lombok.Getter;
import java.util.List;

public abstract class Fuente {
    public abstract List<Hecho> leerHechos();
    public abstract List<Hecho> getHechos();
}
