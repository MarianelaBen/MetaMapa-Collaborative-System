package domain.criterios;

import domain.Hecho;

public class CriterioContenidoMultimedial implements Criterio{
  private String contenidoMultimedial;

  public CriterioContenidoMultimedial(String contenidoMultimedial) {
    this.contenidoMultimedial = contenidoMultimedial;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return contenidoMultimedial.equalsIgnoreCase(hecho.contenidoMultimedia);
    //Lo dejamos implementado como string y más adelante vemos.
  }

}
