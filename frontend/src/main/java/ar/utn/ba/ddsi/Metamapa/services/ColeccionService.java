package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.input.ColeccionInputDTO;
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
    public List<ColeccionDTO> getColeccionesConHechos() {
        // 1) obtener colecciones (salida del backend)
        List<ColeccionInputDTO> colecciones = webClient.get()
                .uri("/colecciones")
                .retrieve()
                .bodyToFlux(ColeccionInputDTO.class)
                .collectList()
                .block();

        if (colecciones == null || colecciones.isEmpty()) {
            return List.of();
        }

        List<ColeccionDTO> resultado = new ArrayList<>();

        // 2) por cada coleccion, pedir sus hechos y mapear
        for (ColeccionInputDTO co : colecciones) {
            List<HechoDTO> hechos;
            try {
                // Asumo endpoint: GET /api/admin/colecciones/{coleccionId}/hechos
                // y que 'coleccionId' se corresponde con co.getHandle()
                hechos = webClient.get()
                        .uri("/colecciones/{coleccionId}/hechos", co.getHandle())
                        .retrieve()
                        .bodyToFlux(HechoDTO.class)
                        .collectList()
                        .block(Duration.ofSeconds(5)); // timeout por coleccion
                if (hechos == null) hechos = List.of();
            } catch (WebClientResponseException.NotFound nf) {
                // si el backend no encuentra, devolvemos lista vacía de hechos
                hechos = List.of();
            } catch (Exception e) {
                // loguear en producción; por ahora devolvemos vacíos para que la UI siga funcionando
                hechos = List.of();
            }

            ColeccionDTO c = new ColeccionDTO(co.getTitulo(), co.getDescripcion(), co.getHandle(), hechos);
            c.setAlgoritmoDeConsenso(co.getAlgoritmoDeConsenso());
            c.setFuenteIds(co.getFuenteIds());
            c.setCriterioIds(co.getCriterioIds());

            resultado.add(c);
        }

        return resultado;
    }
}
