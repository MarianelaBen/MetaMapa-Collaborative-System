package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO; //RESPONSE SERIA IMPUT?
import ar.utn.ba.ddsi.models.dtos.input.HechoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class ApiCatedraService {
  private final WebClient webClient;

  public ApiCatedraService(
      WebClient.Builder webClientBuilder,
      @Value("${proxy.bearer-token}") String bearerToken
  ) {
    this.webClient = webClientBuilder
        .baseUrl("https://api-ddsi.disilab.ar/public/api")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public Mono<List<HechoDTO>> obtenerHechos() {
    return webClient
        .get()
        .uri("/desastres")
        .retrieve()
        .bodyToMono(HechoResponseDTO.class)
        .map(HechoResponseDTO::getData);   // en lugar de getHechos()
  }

  public Mono<HechoDTO> obtenerHechoPorId(long id){
    return webClient
        .get()
        .uri("/desastres/{id}", id)
        .retrieve()
        .bodyToMono(HechoDTO.class);
  }


}
