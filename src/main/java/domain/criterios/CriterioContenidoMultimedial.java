package domain.criterios;

import domain.Hecho;

public class CriterioContenidoMultimedial implements Criterio{
  private String contenidoMultimedial;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return contenidoMultimedial.equals(hecho.contenidoMultimedia);
  }

}
