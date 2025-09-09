package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("origen")
@NoArgsConstructor
public class CriterioOrigen extends Criterio{

  @Column(name = "origen", nullable = false)
  private Origen origen;

  public CriterioOrigen(Origen origen) {
    this.origen = origen;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return origen.equals(hecho.getOrigen());
  }
}
