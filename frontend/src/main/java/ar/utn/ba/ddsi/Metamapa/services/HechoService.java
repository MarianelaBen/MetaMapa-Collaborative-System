package ar.utn.ba.ddsi.Metamapa.services;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HechoService {

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


