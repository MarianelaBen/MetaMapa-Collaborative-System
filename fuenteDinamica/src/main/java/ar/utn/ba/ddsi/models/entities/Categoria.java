package ar.utn.ba.ddsi.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Categoria {
  private String nombre;
  private Integer id;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}
