package domain.criterios;

import domain.Hecho;

public class CriterioLugar implements Criterio{
  private String lugar;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return lugar.equals(hecho.lugar);
  }
}
