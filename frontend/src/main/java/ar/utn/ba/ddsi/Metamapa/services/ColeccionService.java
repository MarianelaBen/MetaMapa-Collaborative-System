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
}
