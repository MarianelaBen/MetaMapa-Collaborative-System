package ar.utn.ba.ddsi.Metamapa.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HechoDTO {
  private String id;
  private String titulo;

  public HechoDTO(String id, String titulo) {
    this.id = id;
    this.titulo = titulo;
  }
}
