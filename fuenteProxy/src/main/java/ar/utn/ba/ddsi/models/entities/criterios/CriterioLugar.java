
package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;

public class CriterioLugar implements Criterio {
  private Ubicacion ubicacion;
  private int rangoMaximo; // num de tolerancia maxima

  public CriterioLugar(Ubicacion ubicacion, int rangoMaximo) {
    this.ubicacion = ubicacion;
    this.rangoMaximo = rangoMaximo;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {
    double distanciaLatitud = Math.abs(hecho.getUbicacion().getLatitud() - ubicacion.getLatitud());
    double distanciaLongitud = Math.abs(hecho.getUbicacion().getLongitud() - ubicacion.getLongitud());
    double distancia = distanciaLatitud + distanciaLongitud;

    return distancia <= rangoMaximo;
  }
}