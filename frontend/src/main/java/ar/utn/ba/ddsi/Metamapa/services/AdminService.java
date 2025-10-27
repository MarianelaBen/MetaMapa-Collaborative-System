package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.InformeDeResultadosDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.PaginaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AdminService {

  private final WebClient webClientPublic;
  private final WebClient webClientAdmin;

  public AdminService(WebClient.Builder webClientBuilder,
                      @Value("${backend.api.base-url-agregador}") String baseUrl,
                      @Value("${backend.api.base-url}") String baseUrlAdmin,
                      ObjectMapper objectMapper) {
    this.webClientPublic = webClientBuilder
        .baseUrl(baseUrl)
        .build();
    this.webClientAdmin = webClientBuilder
        .baseUrl(baseUrlAdmin)
        .build();
  }

  public PaginaDTO<HechoDTO> obtenerHechosPaginado(int page, int size) {
    return webClientPublic.get()
        .uri(uriBuilder -> uriBuilder
            .path("/paginado")
            .queryParam("page", page)
            .queryParam("size", size)
            .build())
        .retrieve()
        .bodyToMono(new org.springframework.core.ParameterizedTypeReference<PaginaDTO<HechoDTO>>() {})
        .block();
  }

  public InformeDeResultadosDTO importarHechosCsv(MultipartFile archivo) {
    MultipartBodyBuilder body = new MultipartBodyBuilder();
    body.part("archivo", archivo.getResource())
        .filename(archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "import.csv")
        .contentType(MediaType.parseMediaType("text/csv"));

    return webClientAdmin.post()
        .uri("/import/hechos/csv")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(body.build()))
        .retrieve()
        .bodyToMono(InformeDeResultadosDTO.class)
        .block();
  }
}
