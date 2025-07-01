package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdapterFuenteProxy {

  private final WebClient webClient;

  @Autowired
  public AdapterFuenteProxy(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public List<Hecho> obtenerHechos(String fuenteUrl) {
    List<HechoInputDTO> hechosDTO = webClient.get()
        .uri(fuenteUrl)
        .retrieve()
        .bodyToFlux(HechoInputDTO.class)
        .collectList()
        .block();

    return hechosDTO.stream()
        .map(HechoInputDTO::toHecho)
        .collect(Collectors.toList());
  }
}