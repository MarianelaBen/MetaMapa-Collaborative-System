package ar.utn.ba.ddsi.Metamapa.dtos;

import java.util.ArrayList;
import java.util.List;

public class ColeccionDTO {
  private String titulo;
  private String descripcion;
  private List<HechoDTO> hechos = new ArrayList<>();

  public ColeccionDTO(String titulo, String descripcion, List<HechoDTO> hechos) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.hechos = hechos;
  }
}
