package ar.utn.ba.ddsi.Metamapa.services;
import ar.utn.ba.ddsi.Metamapa.models.dtos.CategoriaDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HechoService {

    private final WebClient webClientPublic;
    private final WebClient webClientAdmin;
    private final ObjectMapper objectMapper;

    public HechoService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url-agregador}") String baseUrl,
                            @Value("${backend.api.base-url}") String baseUrlAdmin,
                            ObjectMapper objectMapper) {
        this.webClientPublic = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.webClientAdmin = webClientBuilder
                .baseUrl(baseUrlAdmin)
                .build();
        this.objectMapper = objectMapper;
    }

    public List<HechoDTO> getHechos() {
        List<HechoDTO> hechos = webClientPublic.get()
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

    public HechoDTO getHechoPorId(Long id) {
        if (id == null) return null;
        try {
            return webClientPublic.get()
                    .uri("/hechos/{id}", id)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.NOT_FOUND,
                            resp -> Mono.error(new NoSuchElementException("Hecho no encontrado id=" + id)))
                    .bodyToMono(HechoDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // opcional: loguear e devolver null para que el controller lo maneje
            return null;
        }
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
            return webClientPublic.post()
                    .uri("/hechos")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(HechoDTO.class)
                    .block();

        } catch (IOException e) {
            throw new RuntimeException("Error serializando archivos para envío: " + e.getMessage(), e);
        }
    }

    public HechoDTO obtenerHechoPorId(Long id) {
        HechoDTO hecho = webClientPublic.get()
            .uri("/hechos/{id}", id)
            .retrieve()
            .bodyToMono(HechoDTO.class)
            .block();
        if (hecho == null) throw new NoSuchElementException("Hecho no encontrado");
        return hecho;
    }

    public HechoDTO actualizarHecho(Long id, HechoDTO dto, MultipartFile[] multimedia, boolean replaceMedia, List<String> deleteExisting) {
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

            String json = objectMapper.writeValueAsString(dto);
            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> hechoPart = new HttpEntity<>(json, jsonHeaders);
            parts.add("hecho", hechoPart);

            boolean hayArchivos = multimedia != null && Arrays.stream(multimedia).anyMatch(f -> f != null && !f.isEmpty());
            if (hayArchivos) {
                for (MultipartFile file : multimedia) {
                    if (file != null && !file.isEmpty()) {
                        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                            @Override public String getFilename() { return file.getOriginalFilename(); }
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

            if (deleteExisting != null && !deleteExisting.isEmpty()) {
                for (String path : deleteExisting) {
                    // Podés agregar Strings directamente: Spring los manda como text/plain
                    parts.add("deleteExisting", path);
                    // Si preferís ser explícito:
                    // HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.TEXT_PLAIN);
                    // parts.add("deleteExisting", new HttpEntity<>(path, h));
                }
            }

            return webClientPublic.put()
                .uri(uriBuilder -> uriBuilder
                    .path("/hechos/{id}")
                    .queryParam("replaceMedia", replaceMedia)
                    .build(id))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(parts))
                .retrieve()
                .bodyToMono(HechoDTO.class)
                .block();
        } catch (IOException e) {
            throw new RuntimeException("Error serializando archivos para envío: " + e.getMessage(), e);
        }
    }

    public List<String> getCategorias() {
        List<CategoriaDTO> categorias = webClientPublic.get()
            .uri("/categorias")
            .retrieve()
            .bodyToFlux(CategoriaDTO.class)
            .collectList()
            .block();

        if (categorias == null) return List.of();
        return categorias.stream().map(CategoriaDTO::getNombre).toList();
    }

    public void eliminarHecho(Long id){
        try{
            webClientAdmin.post()
                    .uri("/hechos/{id}/eliminar", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }catch(WebClientResponseException e){
            throw new RuntimeException("Error al eliminar hecho: " + e.getResponseBodyAsString(), e);
        }

    }

    public void sumarVistaHecho(Long id) {
        try {
            webClientPublic.post()
                    .uri("/hecho/{id}/vista", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        }catch (WebClientResponseException e){
            throw new RuntimeException("Error al sumar vista al hecho: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<HechoDTO> traerHechosDestacados(){
        try {
            return webClientPublic.get()
                    .uri("/hechos-destacados")
                    .retrieve()
                    .bodyToFlux(HechoDTO.class)
                    .collectList()
                    .block();

        }catch (WebClientResponseException e){
            throw new RuntimeException("Error al traer hechos: " + e.getResponseBodyAsString(), e);
        }
    }


}




