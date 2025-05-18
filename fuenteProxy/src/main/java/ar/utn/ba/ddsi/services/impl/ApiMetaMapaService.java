package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoResponseDTO;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class ApiMetaMapaService {
  private final WebClient webClient;

  public ApiMetaMapaService(@Qualifier("apiMetaMapaClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<List<HechoDTO>> obtenerHechos() { //Puede cambiar, depende como ser la respuesta de la API
    return webClient.get()
        .uri("/hechos")
        .retrieve()
        .bodyToMono(HechoResponseDTO.class) //Puede cambiar, depende como ser la respuesta de la API
        .map(HechoResponseDTO::getData);
  }

}