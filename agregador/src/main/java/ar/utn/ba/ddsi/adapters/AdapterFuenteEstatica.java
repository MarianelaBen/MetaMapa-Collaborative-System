package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AdapterFuenteEstatica {

  private final WebClient webClient;


  public AdapterFuenteEstatica(String url) {
    this.webClient = WebClient.builder().build();
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