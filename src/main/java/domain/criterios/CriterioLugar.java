
package domain.criterios;

import domain.Hecho;

public class CriterioLugar implements Criterio {

  private double latitudReferencia;
  private double longitudReferencia;
  private int rangoMaximo; // num de tolerancia maxima

  public CriterioLugar(double latitudReferencia, double longitudReferencia, int rangoMaximo) {
    this.latitudReferencia = latitudReferencia;
    this.longitudReferencia = longitudReferencia;
    this.rangoMaximo = rangoMaximo;
  }

  @Override
  public boolean cumpleCriterio(Hecho hecho) {
    double distanciaLatitud = Math.abs(hecho.getLatitud() - latitudReferencia);
    double distanciaLongitud = Math.abs(hecho.getLongitud() - longitudReferencia);
    double distancia = distanciaLatitud + distanciaLongitud;

    return distancia <= rangoMaximo;
  }
}