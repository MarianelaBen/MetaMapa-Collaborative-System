package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputComunDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Etiqueta;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdapterFuenteProxy {

  private final WebClient webClient;

  @Autowired
  public AdapterFuenteProxy(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public List<Hecho> obtenerHechos(String fuenteUrl) {
    List<HechoInputComunDTO> hechosDTO = webClient.get()
        .uri(fuenteUrl)
        .retrieve()
        .bodyToFlux(HechoInputComunDTO.class)
        .collectList()
        .block();

    if (hechosDTO == null) return List.of();
    return hechosDTO.stream().map(this::mapToHecho).collect(Collectors.toList());
  }

  private Hecho mapToHecho(HechoInputComunDTO dto) {
    // Ubicación segura (puede venir null)
    Ubicacion ubicacion = (dto.getUbicacion() == null)
        ? new Ubicacion(null, null)
        : new Ubicacion(dto.getUbicacion().getLatitud(), dto.getUbicacion().getLongitud());

    // fuenteExterna tomada de particulares.fuenteExterna (si está)
    String fuenteExterna = null;
    JsonNode p = dto.getParticulares();
    if (p != null) {
      JsonNode fx = p.path("fuenteExterna");
      if (!fx.isMissingNode() && !fx.isNull()) {
        fuenteExterna = fx.asText();
      }
    }

    Hecho hecho = getHecho(dto, ubicacion, fuenteExterna);

    hecho.setFechaCarga(dto.getFechaCarga());                 // LocalDate en el canónico
    hecho.setFueEliminado(Boolean.TRUE.equals(dto.getFueEliminado()));

    // Etiquetas comunes (si vienen)
    if (dto.getEtiquetas() != null && !dto.getEtiquetas().isEmpty()) {
      dto.getEtiquetas().forEach(n -> hecho.agregarEtiqueta(new Etiqueta(n)));
    }

    // Paths multimedia comunes (si vienen)
    if (dto.getPathContenidoMultimedia() != null) {
      hecho.setPathMultimedia(new ArrayList<>(dto.getPathContenidoMultimedia()));
    }

    return hecho;
  }

  private static Hecho getHecho(HechoInputComunDTO dto, Ubicacion ubicacion, String fuenteExterna) {
    return new Hecho(
        dto.getTitulo(),
        dto.getDescripcion(),
        new Categoria(dto.getCategoria()),
        ubicacion,
        dto.getFechaAcontecimiento(),
        dto.getFechaCarga(),
        Origen.CARGA_MANUAL,
        fuenteExterna
    );
  }
}