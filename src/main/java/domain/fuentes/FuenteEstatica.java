package domain.fuentes;

import domain.fuentes.Fuente;
import java.util.Set;
import lombok.Getter;

public class FuenteEstatica extends Fuente {
  @Getter private Set<String> rutas;

  public FuenteEstatica(Set<String> rutas) {
    this.rutas = rutas;
  }

}
