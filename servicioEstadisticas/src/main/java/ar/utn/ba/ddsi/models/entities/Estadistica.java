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

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estadisticas")
public class Estadistica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tipo", nullable = false)
  private String tipo;  // ej: "provincia_mas_hechos", "categoria_mas_hechos", etc.

  @Column(name = "clave", nullable = false)
  private String clave; // ej: nombre provincia o categoría

  @Column(name = "valor", nullable = false)
  private Long valor;   // cantidad

  @Column(name = "fecha_calculo", nullable = false)
  private LocalDateTime fechaCalculo;

  public Estadistica (String tipo, String clave, Long valor, LocalDateTime fechaCalculo) {
    this.tipo = tipo;
    this.clave = clave;
    this.valor = valor;
    this.fechaCalculo = fechaCalculo;
  }
}

