package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("titulo")
@NoArgsConstructor
public class CriterioTitulo extends Criterio{
  @Column(name = "titulo", nullable = false)
  private String titulo;


    @Override
    public boolean cumpleCriterio(Hecho hecho){
        if (hecho.getTitulo() == null) return false;
        return hecho.getTitulo().toLowerCase().contains(titulo.toLowerCase());
    }
}
