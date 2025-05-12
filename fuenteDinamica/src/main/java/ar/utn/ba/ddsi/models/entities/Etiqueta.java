package ar.utn.ba.ddsi.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Etiqueta {
  private String nombre;
  private Integer id;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}
