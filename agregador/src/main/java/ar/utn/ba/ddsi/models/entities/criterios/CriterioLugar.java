
package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("lugar")
@NoArgsConstructor
public class CriterioLugar extends Criterio {

  @Column(name = "ubicacion", nullable = false)
  private Ubicacion ubicacion;

  @Column(name = "rango_maximo", nullable = false)
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