package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("fecha_carga")
@NoArgsConstructor
public class CriterioFechaCarga extends Criterio{

  @Column(name = "carga_desde", nullable = false)
  private LocalDate desde;

  @Column(name = "carga_hasta", nullable = false)
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
