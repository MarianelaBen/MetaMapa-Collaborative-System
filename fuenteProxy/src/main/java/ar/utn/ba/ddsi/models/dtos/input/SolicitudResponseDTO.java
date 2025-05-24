package ar.utn.ba.ddsi.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class SolicitudResponseDTO {
  private List<SolicitudDTO> data;
}
