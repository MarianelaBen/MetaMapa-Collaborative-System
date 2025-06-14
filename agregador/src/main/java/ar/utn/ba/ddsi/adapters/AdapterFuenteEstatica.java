package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AdapterFuenteEstatica {

  private final WebClient webClient;

  public AdapterFuenteEstatica( ) {
    this.webClient = WebClient.builder()
        .baseUrl("http://fuente-estatica:8081/api/hechos")
        .build();
  }

  public List<Hecho> obtenerHechos() {
    return webClient.get()
        .retrieve()
        .bodyToFlux(Hecho.class)
        .collectList()
        .block();
  }
}