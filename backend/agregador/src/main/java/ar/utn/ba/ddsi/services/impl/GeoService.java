package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.GeoSugerenciaDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoService {

  private final WebClient georefWebClient;

  public GeoService(WebClient georefWebClient) {
    this.georefWebClient = georefWebClient;
  }

  public List<GeoSugerenciaDTO> buscarSugerencias(String query, String provinciaId, int max) {
    try {
      String q = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
      String url = "/direcciones?direccion=" + q + "&max=" + max;
      if (provinciaId != null && !provinciaId.isBlank()) {
        url += "&provincia=" + URLEncoder.encode(provinciaId, StandardCharsets.UTF_8);
      }

      JsonNode root = georefWebClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(JsonNode.class)
          .block();

      List<GeoSugerenciaDTO> out = new ArrayList<>();
      if (root == null) return out;

      JsonNode direcciones = root.get("direcciones");
      if (direcciones == null || !direcciones.isArray()) return out;

      for (JsonNode d : direcciones) {
        String label = text(d, "nomenclatura"); // Georef suele devolver un "nomenclatura"
        JsonNode ubic = d.get("ubicacion");
        Double lat = ubic != null && ubic.has("lat") ? ubic.get("lat").asDouble() : null;
        Double lon = ubic != null && ubic.has("lon") ? ubic.get("lon").asDouble() : null;

        JsonNode provincia = d.get("provincia");
        String provId = provincia != null && provincia.has("id") ? provincia.get("id").asText() : null;
        String provNombre = provincia != null && provincia.has("nombre") ? provincia.get("nombre").asText() : null;

        // opcionales
        String calle = nodePath(d, "calle", "nombre");
        String altura = nodePath(d, "altura", "valor");
        String localidad = nodePath(d, "localidad", "nombre");

        GeoSugerenciaDTO sug = new GeoSugerenciaDTO(
            label, lat, lon, provId, provNombre, calle, altura, localidad
        );

        // Evitamos devolver entradas sin coordenadas o sin provincia
        if (lat != null && lon != null && provNombre != null) {
          out.add(sug);
        }
      }
      return out;
    } catch (Exception e) {
      // En prod: loggear, quizá fallback vacío
      return List.of();
    }
  }

  private static String text(JsonNode n, String field) {
    return (n != null && n.has(field) && !n.get(field).isNull()) ? n.get(field).asText() : null;
  }
  private static String nodePath(JsonNode n, String child, String field) {
    JsonNode c = (n != null) ? n.get(child) : null;
    return (c != null && c.has(field) && !c.get(field).isNull()) ? c.get(field).asText() : null;
  }
}
