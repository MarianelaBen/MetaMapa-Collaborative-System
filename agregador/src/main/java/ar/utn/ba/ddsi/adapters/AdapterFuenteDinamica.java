package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Component
public class AdapterFuenteDinamica {

    private final WebClient webClient;

    @Autowired
    public AdapterFuenteDinamica(WebClient.Builder webClientBuilder) {
      this.webClient = webClientBuilder.build();
    }

    public List<Hecho> obtenerHechos(String fuenteUrl) {
      List<HechoOutputDTO> hechosDTO = webClient.get()
          .uri(fuenteUrl)
          .retrieve()
          .bodyToFlux(HechoOutputDTO.class)
          .collectList()
          .block();

      return hechosDTO.stream()
          .map(HechoOutputDTO::toHecho)
          .collect(Collectors.toList());
    }

}
