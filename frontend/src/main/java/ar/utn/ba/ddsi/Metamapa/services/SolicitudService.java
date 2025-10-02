package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.SolicitudDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudService {
    private final WebClient webClient;

    public SolicitudService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url-agregador}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public List<SolicitudDTO> getSolicitudes() {
        // 1) obtener colecciones (salida del backend)
        List<SolicitudDTO> solicitudes = webClient.get()
                .uri("/solicitudes")
                .retrieve()
                .bodyToFlux(SolicitudDTO.class)
                .collectList()
                .block();

        if (solicitudes == null || solicitudes.isEmpty()) {
            return List.of();
        }

        return solicitudes;
    }

  public String crearSolicitudEliminacion(Long hechoId, String justificacion) { //TODO POR AHORA AHARDCODEADO DSP CONECTAR CONM BACK
    String shortId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    return "SOL-" + shortId;
  }
}
