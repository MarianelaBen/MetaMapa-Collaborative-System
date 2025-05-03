package ar.utn.ba.ddsi.MetaMapa.models.entities.criterios;

import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;

public class CriterioTitulo implements Criterio{
  private String titulo;

  public CriterioTitulo(String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho){

    return titulo.equalsIgnoreCase(hecho.getTitulo());
  }
}
