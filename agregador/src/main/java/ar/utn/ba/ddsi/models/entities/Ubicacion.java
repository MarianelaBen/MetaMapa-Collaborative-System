package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class Ubicacion {
  @Column(name = "latitud",  nullable = false)
  @Getter private Double latitud;

  @Column(name = "longitud", nullable = false)
  @Getter private Double longitud;

  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }
}
