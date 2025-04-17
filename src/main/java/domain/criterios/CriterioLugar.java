package domain.criterios;

import domain.Hecho;

public class CriterioLugar implements Criterio{
  private String lugar;

  public CriterioLugar(String lugar) {
    this.lugar = lugar;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return lugar.equalsIgnoreCase(hecho.lugar);
  }
}
