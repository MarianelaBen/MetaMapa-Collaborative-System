package domain.criterios;

import domain.Hecho;
import java.time.LocalDateTime;

public class CriterioFechaCarga implements Criterio{
  private LocalDateTime desde;
  private LocalDateTime hasta;

  public CriterioFechaCarga(LocalDateTime desde, LocalDateTime hasta) {
    this.desde = desde;
    this.hasta = hasta;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return hecho.fechaCarga.isAfter(desde) && hecho.fechaCarga.isBefore(hasta);
  }
}
