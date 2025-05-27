package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoResponseDTO;
import ar.utn.ba.ddsi.services.IApiCatedraService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiCatedraService implements IApiCatedraService {

  private final WebClient webClient;

  public ApiCatedraService(@Qualifier("apiClient") WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Mono<List<HechoInputDTO>> obtenerHechos() {
    return webClient.get()
        .uri("/desastres")
        .retrieve()
        .bodyToMono(HechoResponseDTO.class)
        .map(HechoResponseDTO::getData);
  }

  @Override
  public Mono<HechoInputDTO> obtenerHechoPorId(long id) {
    return webClient.get()
        .uri("/desastres/{id}", id)
        .retrieve()
        .bodyToMono(HechoInputDTO.class);
  }
}