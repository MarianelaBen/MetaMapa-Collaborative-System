package domain.criterios;

import domain.Hecho;

public class CriterioLugar extends Criterio{
  private String lugar;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return lugar.equals(hecho.lugar);
  }
}
