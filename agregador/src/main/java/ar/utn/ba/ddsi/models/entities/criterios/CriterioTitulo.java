package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("titulo")
public class CriterioTitulo extends Criterio{
  @Column(name = "titulo", nullable = false)
  private String titulo;


  @Override
  public boolean cumpleCriterio(Hecho hecho){
    return titulo.equalsIgnoreCase(hecho.getTitulo());
  }
}
