package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;

public interface Criterio {

  public abstract boolean cumpleCriterio(Hecho hecho);
}
