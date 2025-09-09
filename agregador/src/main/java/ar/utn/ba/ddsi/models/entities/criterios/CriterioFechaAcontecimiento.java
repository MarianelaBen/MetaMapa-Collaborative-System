package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Entity
@DiscriminatorValue("fecha_acontecimiento")
@NoArgsConstructor
public class CriterioFechaAcontecimiento extends Criterio{

  @Column(name = "desde", nullable = false)
  private LocalDate desde;

  @Column(name = "hasta", nullable = false)
  private LocalDate hasta;

  public CriterioFechaAcontecimiento(LocalDate desde, LocalDate hasta){
    this.desde = desde;
    this.hasta = hasta;
  }


  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return hecho.getFechaAcontecimiento().isAfter(desde) && hecho.getFechaAcontecimiento().isBefore(hasta);
  }
}
