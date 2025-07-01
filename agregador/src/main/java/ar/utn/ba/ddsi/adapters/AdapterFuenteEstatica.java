package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AdapterFuenteEstatica {

  private final WebClient webClient;

@Autowired
  public AdapterFuenteEstatica(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public List<Hecho> obtenerHechos(String fuenteUrl) {
    return webClient.get()
        .uri(fuenteUrl)
        .retrieve()
        .bodyToFlux(Hecho.class)
        .collectList()
        .block();
  }
}