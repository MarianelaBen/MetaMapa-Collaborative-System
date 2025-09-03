package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "categoria")
@Getter
public class Categoria {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //TODO como se normaliza las mayusculas, y eso
  @Column(nullable = false, unique = true)
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}
