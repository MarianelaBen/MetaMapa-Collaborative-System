package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.MetaMapa.models.entities.Categoria;
import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;

public class CriterioCategoria implements Criterio{
  private Categoria categoria;

  public CriterioCategoria(Categoria categoria){
    this.categoria = categoria;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {

    return categoria.getNombre().equalsIgnoreCase(hecho.getCategoria().getNombre());
  }

}
