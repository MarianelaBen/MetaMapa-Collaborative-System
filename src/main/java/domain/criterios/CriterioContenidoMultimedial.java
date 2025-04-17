package domain.criterios;

import domain.Hecho;

public class CriterioContenidoMultimedial extends Criterio{
  private String contenidoMultimedial;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return contenidoMultimedial.equals(hecho.contenidoMultimedia);
  }

}
