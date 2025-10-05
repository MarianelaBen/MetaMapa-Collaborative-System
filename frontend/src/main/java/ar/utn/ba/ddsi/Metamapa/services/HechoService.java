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

    public HechoDTO getHechoPorId(Long id) {
        HechoDTO hecho = webClient.get()
            .uri("/hechos/{id}", id)
            .retrieve()
            .bodyToMono(HechoDTO.class)
            .block();

        if (hecho == null)
            throw new NoSuchElementException("Hecho no encontrado id=" + id);
        return hecho;
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

    // HechoService.java
    public HechoDTO obtenerHechoPorId(Long id) {
        HechoDTO hecho = webClient.get()
            .uri("/hechos/{id}", id)
            .retrieve()
            .bodyToMono(HechoDTO.class)
            .block();
        if (hecho == null) throw new NoSuchElementException("Hecho no encontrado");
        return hecho;
    }

    public HechoDTO actualizarHecho(Long id, HechoDTO dto, MultipartFile[] multimedia, boolean replaceMedia) {
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

            return webClient.put()
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

}




