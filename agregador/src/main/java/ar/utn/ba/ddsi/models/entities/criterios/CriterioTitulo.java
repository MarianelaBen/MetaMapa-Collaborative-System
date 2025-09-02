package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;

public class tituCriterioTitulo implements Criterio{
  private String titulo;

  public CriterioTitulo(String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho){

    return titulo.equalsIgnoreCase(hecho.getTitulo());
  }
}
