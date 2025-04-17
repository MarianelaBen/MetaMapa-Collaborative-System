package domain.criterios;

import domain.Hecho;

public class CriterioTitulo extends Criterio{
  private String titulo;

  @Override
  public boolean cumpleCriterio(Hecho hecho){

    return titulo.equalsIgnoreCase(hecho.titulo);
  }
}
