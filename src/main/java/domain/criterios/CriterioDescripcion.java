package domain.criterios;

import domain.Hecho;

public class CriterioDescripcion extends Criterio{
    private String descripcion;

  @Override
  public boolean cumpleCriterio(Hecho hecho){

    return descripcion.equals(hecho.descripcion);
  }
}
