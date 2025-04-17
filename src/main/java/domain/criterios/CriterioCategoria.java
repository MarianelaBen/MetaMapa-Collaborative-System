package domain.criterios;

import domain.Hecho;

public class CriterioCategoria implements Criterio{
  private String categoria;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return categoria.equalsIgnoreCase(hecho.categoria);
  }

}
