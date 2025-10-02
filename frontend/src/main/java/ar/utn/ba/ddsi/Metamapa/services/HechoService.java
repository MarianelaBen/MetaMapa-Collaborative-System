package ar.utn.ba.ddsi.Metamapa.services;
import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HechoService {
    private final WebClient webClient;

    public HechoService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    //TODO: falta modificar método de obtención de hechos en el back
    public List<HechoDTO> getHechos() {
        List<HechoDTO> hechos = webClient.get()
                .uri("/hechos")
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();

        if (hechos == null || hechos.isEmpty()) {
            return List.of();
        }

        return hechos;
    }

  /*
  private final WebClient backendWebClient;

  public HechoDTO obtenerHechoPorId(Long id) {
    try {
      return backendWebClient.get()
          .uri("/api/public/hechos/fuentes")
          .retrieve()
          .bodyToFlux(HechoDTO.class)  // la API devuelve una lista/stream de DTOs
          .filter(h -> h.getId() != null && h.getId().equals(id))
          .next()                      // toma el primero que matchee
          .blockOptional()
          .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + id));

    } catch (Exception e) {
      // Fallback temporal para no romper la UI mientras integrás todo
      System.err.println("[HechoService] Error o backend caído, uso mock. Causa: " + e.getMessage());
      HechoDTO mock = new HechoDTO(
          "Incendio forestal activo en Parque Nacional Los Glaciares",
          "Incendio de gran magnitud detectado en el sector norte del parque. Las llamas avanzan " +
              "sobre zona de bosque nativo y requieren coordinación de brigadas aéreas y terrestres.",
          "Incendio forestal",
          LocalDateTime.of(2025, 8, 12, 9, 15),
          "Santa Cruz"
      );
      mock.setId(id);
      mock.setLatitud(-50.2938);
      mock.setLongitud(-73.0138);
      mock.setFuenteExterna("https://ejemplo.com/fuente");
      mock.setIdContenidoMultimedia(List.of("foto1.jpg","video1.mp4","grafico1.png"));
      return mock;
    }
  }
*/
}


