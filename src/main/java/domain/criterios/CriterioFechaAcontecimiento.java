package domain.criterios;

import domain.Hecho;
import java.time.LocalDateTime;

public class CriterioFechaAcontecimiento extends Criterio{
  private LocalDateTime fechaAcontecimiento;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return fechaAcontecimiento.equals(hecho.fechaAcontecimmiento);
  }
}
