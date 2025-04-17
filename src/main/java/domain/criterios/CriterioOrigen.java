package domain.criterios;

import domain.Hecho;
import domain.enumerados.Origen;

public class CriterioOrigen extends Criterio{
  private Origen origen;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return origen.equals(hecho.origen);
  }
}
