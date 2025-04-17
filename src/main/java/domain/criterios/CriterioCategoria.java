package domain.criterios;

import domain.Hecho;

public class CriterioCategoria extends Criterio{
  private String categoria;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return categoria.equals(hecho.categoria);
  }

}
