package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputComunDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Component
public class AdapterFuenteDinamica {

    private final WebClient webClient;

    @Autowired
    public AdapterFuenteDinamica(WebClient.Builder webClientBuilder) {
      this.webClient = webClientBuilder.build();
    }

    public List<Hecho> obtenerHechos(String fuenteUrl) {
      List<HechoInputComunDTO> hechosDTO = webClient.get()
          .uri(fuenteUrl)
          .retrieve()
          .bodyToFlux(HechoInputComunDTO.class)
          .collectList()
          .block();

      if (hechosDTO == null) {return List.of();}
      return hechosDTO.stream().map(this::mapToHecho).collect(Collectors.toList());
    }

    public Hecho mapToHecho(HechoInputComunDTO dto) {
    Hecho hecho = new Hecho(
        dto.getTitulo(),
        dto.getDescripcion(),
        new Categoria(dto.getCategoria()),
        new Ubicacion(
            dto.getUbicacion() != null ? dto.getUbicacion().getLatitud() : null,
            dto.getUbicacion() != null ? dto.getUbicacion().getLongitud() : null
        ),
        dto.getFechaAcontecimiento(),
        dto.getFechaCarga(),
        Origen.PROVISTO_POR_CONTRIBUYENTE,
         null
    );
      System.out.println("hola2");
      System.out.println(dto.getCategoria());
      System.out.println(hecho.getCategoria().getNombre());

    hecho.setFueEliminado(dto.getFueEliminado());
    if (dto.getPathMultimedia() != null) {hecho.setPathMultimedia(dto.getPathMultimedia());}
    if (dto.getEtiquetas() != null) {
      for (String nombre : dto.getEtiquetas()) {hecho.agregarEtiqueta(new Etiqueta(nombre));}}


    JsonNode particulares = dto.getParticulares();
      if (particulares != null) {
        JsonNode contribuyenteARecuperar = particulares.path("contribuyente");
        if (!contribuyenteARecuperar.isMissingNode()) {
          Contribuyente contribuyente = new Contribuyente(
              textOrNull(contribuyenteARecuperar, "nombre"),
              dateOrNull(contribuyenteARecuperar, "fechaDeNacimiento"),
              textOrNull(contribuyenteARecuperar, "apellido")
          );
          hecho.setContribuyente(contribuyente);
        }

        JsonNode paths = particulares.path("pathContenidoMultimedia");
        if (paths.isArray()) {
          List<String> list = new ArrayList<>();
          paths.forEach(n -> list.add(n.asText()));
          hecho.setPathMultimedia(list);
        }
      }

      return hecho;
    }

  private static String textOrNull(JsonNode node, String field) {
    if (node == null) return null;
    JsonNode v = node.path(field);
    return (v.isMissingNode() || v.isNull()) ? null : v.asText();
  }

  private static LocalDate dateOrNull(JsonNode node, String field) {
    String s = textOrNull(node, field);
    if (s == null) return null;
    try {
      return LocalDate.parse(s); // ISO-8601 (YYYY-MM-DD)
    } catch (Exception e) {
      return null;
    }
  }
}
