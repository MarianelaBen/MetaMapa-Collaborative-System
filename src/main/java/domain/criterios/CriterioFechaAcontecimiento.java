package domain.criterios;

import domain.Hecho;
import java.time.LocalDateTime;

public class CriterioFechaAcontecimiento implements Criterio{
  private LocalDateTime desde;
  private LocalDateTime hasta;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return hecho.fechaAcontecimiento.isAfter(desde) && hecho.fechaAcontecimiento.isBefore(hasta);
  }
}
