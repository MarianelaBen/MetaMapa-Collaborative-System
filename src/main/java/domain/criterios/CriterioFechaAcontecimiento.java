package domain.criterios;

import domain.Hecho;
import java.time.LocalDate;

public class CriterioFechaAcontecimiento implements Criterio{
  private LocalDate desde;
  private LocalDate hasta;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return hecho.fechaAcontecimiento.isAfter(desde) && hecho.fechaAcontecimiento.isBefore(hasta);
  }
}
