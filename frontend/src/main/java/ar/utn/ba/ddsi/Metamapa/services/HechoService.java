package ar.utn.ba.ddsi.Metamapa.services;
import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HechoService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public HechoService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url-agregador}" ) String baseUrl, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

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

    public HechoDTO subirHecho(HechoDTO dto, MultipartFile[] multimedia) {
        try {
            // Construimos multipart
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

            // Parte JSON con content-type application/json
            String json = objectMapper.writeValueAsString(dto);
            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> hechoPart = new HttpEntity<>(json, jsonHeaders);
            parts.add("hecho", hechoPart);


            // Solo añadir archivos si realmente hay alguno no vacío
            boolean hayArchivos = multimedia != null && Arrays.stream(multimedia)
                    .anyMatch(f -> f != null && !f.isEmpty());

            // partes de archivos
            if (hayArchivos) {
                for (MultipartFile file : multimedia) {
                    if (file != null && !file.isEmpty()) {
                        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                            @Override
                            public String getFilename() {
                                return file.getOriginalFilename();
                            }
                        };
                        HttpHeaders fileHeaders = new HttpHeaders();
                        fileHeaders.setContentDispositionFormData("multimedia", file.getOriginalFilename());
                        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
                        fileHeaders.setContentType(MediaType.parseMediaType(contentType));
                        HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(resource, fileHeaders);
                        parts.add("multimedia", filePart);
                    }
                }
            }

            // Hacemos la petición
            return webClient.post()
                    .uri("/hechos") // si tu baseUrl es http://localhost:8083/api/public
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(HechoDTO.class)
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("Error serializando archivos para envío: " + e.getMessage(), e);
        }
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


