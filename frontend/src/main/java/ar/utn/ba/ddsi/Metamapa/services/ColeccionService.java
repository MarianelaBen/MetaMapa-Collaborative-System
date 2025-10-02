package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
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
    public List<ColeccionDTO> getColecciones(){
        try {
            return webClient.get()
                    .uri("/colecciones") // baseUrl ya contiene /api/admin
                    .retrieve()
                    .bodyToFlux(ColeccionDTO.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(5))
                    .block(); // bloquea; ok si tu app no es reactiva
        } catch (WebClientResponseException e) {
            // manejar 4xx/5xx (e.getStatusCode(), e.getResponseBodyAsString())
            e.printStackTrace();
            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
