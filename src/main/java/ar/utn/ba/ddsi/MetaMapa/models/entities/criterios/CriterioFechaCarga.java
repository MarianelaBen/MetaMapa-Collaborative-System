package ar.utn.ba.ddsi.MetaMapa.models.entities.criterios;

import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;
import java.time.LocalDate;

public class CriterioFechaCarga implements Criterio{
  private LocalDate desde;
  private LocalDate hasta;

  public CriterioFechaCarga(LocalDate desde, LocalDate hasta) {
    this.desde = desde;
    this.hasta = hasta;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return hecho.getFechaCarga().isAfter(desde) && hecho.getFechaCarga().isBefore(hasta);
  }
}
