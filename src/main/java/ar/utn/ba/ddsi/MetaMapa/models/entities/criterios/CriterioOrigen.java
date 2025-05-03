package ar.utn.ba.ddsi.MetaMapa.models.entities.criterios;

import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;
import ar.utn.ba.ddsi.MetaMapa.models.entities.enumerados.Origen;

public class CriterioOrigen implements Criterio{
  private Origen origen;

  public CriterioOrigen(Origen origen) {
    this.origen = origen;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return origen.equals(hecho.getOrigen());
  }
}
