package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.models.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

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

  public List<CategoriaDTO> obtenerCategorias() {
      return webClientPublic.get()
              .uri("/categorias")
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<List<CategoriaDTO>>() {})
              .block();
  }

    public void eliminarCategoria(Long id){
        try {
            webClientAdmin
                    .delete()
                    .uri("/categorias/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al eliminar categoria: " + e.getResponseBodyAsString(), e);
        }
    }

    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoria) {
        try {
            return webClientAdmin
                    .put()
                    .uri("/categorias/{id}", id)
                    .bodyValue(categoria)
                    .retrieve()
                    .bodyToMono(CategoriaDTO.class)
                    .block();
        } catch (WebClientResponseException e) {

            throw new RuntimeException("Error al actualizar categoria: " + e.getResponseBodyAsString(), e);
        }
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

    public CategoriaDTO crearCategoria(CategoriaDTO categoria) {
        return webClientAdmin
                .post()
                .uri("/categorias")
                .bodyValue(categoria)
                .retrieve()
                .bodyToMono(CategoriaDTO.class)
                .block();
    }
}
