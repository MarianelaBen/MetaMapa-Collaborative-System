package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "contribuyente")
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
  private String nombre;
  @Column(name = "apellido")
  private String apellido;
  @Column(name = "fecha_nacimiento")
  private LocalDate fechaDeNacimiento;

  public Contribuyente(String nombre, LocalDate fechaDeNacimiento, String apellido) {
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
