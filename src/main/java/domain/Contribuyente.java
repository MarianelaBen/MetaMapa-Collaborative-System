package domain;

import java.time.LocalDate;

public class Contribuyente {
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;

  public Contribuyente(String nombre, LocalDate fechaDeNacimiento, String apellido) {
    this.nombre = nombre;
    this.fechaDeNacimiento = fechaDeNacimiento;
    this.apellido = apellido;
  }

}
