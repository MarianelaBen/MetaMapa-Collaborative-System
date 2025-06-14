package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdapterFuenteDinamica {

    private final WebClient webClient;

    public AdapterFuenteDinamica( ) {
      this.webClient = WebClient.builder()
          .baseUrl("http://fuente-dinamica:8080/api/hechos")
          .build();
    }

    public List<Hecho> obtenerHechos( ) {
      List<HechoOutputDTO> hechosDTO = webClient.get()
          .retrieve()
          .bodyToFlux(HechoOutputDTO.class)
          .collectList()
          .block();

      return hechosDTO.stream()
          .map(HechoOutputDTO::toHecho)
          .collect(Collectors.toList());
    }

}
