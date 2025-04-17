package domain.criterios;

import domain.Hecho;

public class CriterioDescripcion implements Criterio{
    private String descripcion;

  @Override
  public boolean cumpleCriterio(Hecho hecho){

    return descripcion.equals(hecho.descripcion);
  }
}
