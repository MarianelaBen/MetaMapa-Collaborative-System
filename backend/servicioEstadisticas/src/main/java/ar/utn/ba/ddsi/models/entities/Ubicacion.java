package ar.utn.ba.ddsi.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Ubicacion {
  private Double latitud;
  private Double longitud;
  private String provincia;
}