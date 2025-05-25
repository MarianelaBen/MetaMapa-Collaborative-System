package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiCatedraService {

  private final WebClient webClient;

  public ApiCatedraService(@Qualifier("apiClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<List<HechoDTO>> obtenerHechos() {
    return webClient.get()
        .uri("/desastres")
        .retrieve()
        .bodyToMono(HechoResponseDTO.class)
        .map(HechoResponseDTO::getData);
  }

  public Mono<HechoDTO> obtenerHechoPorId(long id) {
    return webClient.get()
        .uri("/desastres/{id}", id)
        .retrieve()
        .bodyToMono(HechoDTO.class);
    //TODO ver si necesita mapeo
  }
}