package domain.criterios;

import domain.Hecho;

public class CriterioCategoria implements Criterio{
  private String categoria;

  public CriterioCategoria(String categoria){
    this.categoria = categoria;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return categoria.equalsIgnoreCase(hecho.getCategoria().getNombre());
  }

}
