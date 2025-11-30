package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Contribuyente {
  @Column(name = "contribuyente_id")
  private Long id;
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;

  public Contribuyente(Long id,String nombre, LocalDate fechaDeNacimiento, String apellido) {
    this.id = id;
    this.nombre = nombre;
    this.fechaDeNacimiento = fechaDeNacimiento;
    this.apellido = apellido;
  }

  public Integer getEdad(){
    LocalDate hoy = LocalDate.now();
    Period periodo = Period.between(fechaDeNacimiento, hoy);
    return periodo.getYears();
  }

}
