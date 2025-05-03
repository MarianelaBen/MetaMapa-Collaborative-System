package ar.utn.ba.ddsi.MetaMapa.models.entities;


import lombok.Getter;

public class Administrador {
  @Getter private String nombre;
  @Getter private String apellido;

  public Administrador(String nombre, String apellido) {
    this.nombre = nombre;
    this.apellido = apellido;
  }
}

