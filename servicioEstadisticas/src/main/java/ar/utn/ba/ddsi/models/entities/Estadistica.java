package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estadistica")
public class Estadistica {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pregunta_id", nullable=false)
  private Pregunta pregunta;

  @Column(name = "coleccion_handle")
  private String coleccionHandle;

  @Column (name = "categoria_id")
  private Long categoriaId;

  @Column(name="provincia")
  private String provincia;

  @Column(name="hora_del_dia")
  private Integer horaDelDia;

  @Column(name="valor", nullable=false)
  private Long valor;

  @Column(name="fecha_de_calculo", nullable=false)
  private LocalDateTime fechaDeCalculo;

  public Estadistica(Pregunta pregunta, String coleccionHandle, Long categoriaId,
                     String provincia, Integer horaDelDia, Long valor, LocalDateTime fechaDeCalculo) {
    this.pregunta = pregunta;
    this.coleccionHandle = coleccionHandle;
    this.categoriaId = categoriaId;
    this.provincia = provincia;
    this.horaDelDia = horaDelDia;
    this.valor = valor;
    this.fechaDeCalculo = fechaDeCalculo;
  }
}

