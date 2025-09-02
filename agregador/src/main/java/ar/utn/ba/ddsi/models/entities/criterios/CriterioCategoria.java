package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("categoria")
public class CriterioCategoria extends Criterio{

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  public CriterioCategoria(Categoria categoria){
    this.categoria = categoria;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return categoria.getNombre().equalsIgnoreCase(hecho.getCategoria().getNombre());
  }

}
