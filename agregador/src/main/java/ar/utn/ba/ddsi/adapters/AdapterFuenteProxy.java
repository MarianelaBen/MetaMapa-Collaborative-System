/*package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdapterFuenteProxy {

  private final WebClient webClient;

  public AdapterFuenteProxy() {
    this.webClient = WebClient.builder()
        .baseUrl("http://fuente-proxy:8082/api/hechos")
        .build();
  }

  public List<Hecho> obtenerHechos() {
    List<HechoInputDTO> hechosDTO = webClient.get()
        .retrieve()
        .bodyToFlux(HechoInputDTO.class)
        .collectList()
        .block();

    return hechosDTO.stream()
        .map(HechoInputDTO::toHecho)
        .collect(Collectors.toList());
  }
}*/