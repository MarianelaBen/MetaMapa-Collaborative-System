package ar.utn.ba.ddsi.models.dtos.output;

public class ProvinciaOutputDTO {
  private String provincia;
  private Long cantidad;

public ProvinciaOutputDTO(String provincia, Long cantidad){
  this.provincia = provincia;
  this.cantidad = cantidad;
}
}
