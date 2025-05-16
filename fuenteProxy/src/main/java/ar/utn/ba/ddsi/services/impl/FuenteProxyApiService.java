package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO; //RESPONSE SERIA IMPUT?
import ar.utn.ba.ddsi.models.dtos.input.HechosReponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class FuenteProxyApiService  {
  private final WebClient webClient;

  public FuenteProxyApiService(WebClient.Builder webClientBuilder){
    this.webClient = webClientBuilder.baseUrl("").build();
  }

  public Mono<List<HechoDTO>> obtenerHechos() {
    return webClient.get()
        .uri("/hechos") //TODO completar
        .retrieve()
        .bodyToMono(HechosReponseDTO.class)
        .map(HechosReponseDTO::getHechos);
  }


}
