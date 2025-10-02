package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ColeccionService {
    private final WebClient webClient;

    public ColeccionService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
    public List<ColeccionDTO> getColecciones() {
        // 1) obtener colecciones (salida del backend)
        List<ColeccionDTO> colecciones = webClient.get()
                .uri("/colecciones")
                .retrieve()
                .bodyToFlux(ColeccionDTO.class)
                .collectList()
                .block();

        if (colecciones == null || colecciones.isEmpty()) {
            return List.of();
        }

        return colecciones;
    }

    public ColeccionDTO getColeccionByHandle(String handle) {
        try {
            ColeccionDTO coleccion = webClient.get()
                    .uri("/colecciones/{handle}", handle)
                    .retrieve()
                    .bodyToMono(ColeccionDTO.class)
                    .block();

            if (coleccion == null) throw new NoSuchElementException("Colección no encontrada: " + handle);

            return coleccion;
        } catch (WebClientResponseException.NotFound nf) {
            throw new NoSuchElementException("Colección no encontrada: " + handle);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la colección: " + e.getMessage(), e);
        }
    }

    public List<HechoDTO> getHechosDeColeccion(String handle) {
        try {
            List<HechoDTO> hechos = webClient.get()
                    .uri("/colecciones/{handle}/hechos", handle)
                    .retrieve()
                    .bodyToFlux(HechoDTO.class)
                    .collectList()
                    .block();

            return hechos == null ? new ArrayList<>() : hechos;
        } catch (WebClientResponseException.NotFound nf) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener hechos de la colección: " + e.getMessage(), e);
        }
    }
}
