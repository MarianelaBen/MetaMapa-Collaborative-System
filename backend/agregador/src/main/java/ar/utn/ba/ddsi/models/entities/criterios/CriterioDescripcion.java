package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("descripcion")
@NoArgsConstructor
public class CriterioDescripcion extends Criterio{
  @Column(name = "descripcion", nullable = false)
  private String descripcion;

  public CriterioDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

    @Override
    public boolean cumpleCriterio(Hecho hecho){
        if (hecho.getDescripcion() == null) return false;
        return hecho.getDescripcion().toLowerCase().contains(descripcion.toLowerCase());
    }
}
