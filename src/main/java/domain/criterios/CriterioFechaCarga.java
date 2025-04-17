package domain.criterios;

import domain.Hecho;
import java.time.LocalDateTime;

public class CriterioFechaCarga extends Criterio{
  private LocalDateTime fechaCarga;

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return fechaCarga.equals(hecho.fechaCarga);
  }
}
