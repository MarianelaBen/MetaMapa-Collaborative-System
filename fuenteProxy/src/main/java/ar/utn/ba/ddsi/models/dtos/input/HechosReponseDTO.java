package ar.utn.ba.ddsi.models.dtos.input;

import lombok.Data;
import java.util.List;

@Data
public class HechosReponseDTO {
  private List<HechoDTO> hechos;
  //mapeo la respuesta que llega de la API externa
}
