package ar.utn.ba.ddsi.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContenidoMultimedia {
  private byte[] datosMultimedia;
  //private String tipoContenido;  // "image/png", "video/mp4"
  private Long idContenidoMultimedia;
}
