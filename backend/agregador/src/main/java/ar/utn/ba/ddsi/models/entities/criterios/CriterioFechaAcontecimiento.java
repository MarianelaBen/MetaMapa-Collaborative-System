package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("fecha_acontecimiento")
@NoArgsConstructor
public class CriterioFechaAcontecimiento extends Criterio{

  @Column(name = "fecha_acontecimiento_desde", nullable = false)
  private LocalDateTime desde;

  @Column(name = "fecha_acontecimiento_hasta", nullable = false)
  private LocalDateTime hasta;

  public CriterioFechaAcontecimiento(LocalDateTime desde, LocalDateTime hasta){
    this.desde = desde;
    this.hasta = hasta;
  }

    @Override
    public boolean cumpleCriterio(Hecho hecho) {
        if (hecho.getFechaAcontecimiento() == null) return false;

        LocalDateTime fecha = hecho.getFechaAcontecimiento();

        boolean pasoInicio = !fecha.isBefore(desde);
        boolean pasoFin = !fecha.isAfter(hasta);

        return pasoInicio && pasoFin;
    }
}
