package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.ColeccionResponseDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoResponseDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
/*
@Service
public class ApiMetaMapaService {
  private final WebClient webClient;

  public ApiMetaMapaService(@Qualifier("apiMetaMapaClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<List<HechoInputDTO>> obtenerHechos() { //Puede cambiar, depende como ser la respuesta de la API
    return webClient.get()
        .uri("/hechos")
        .retrieve()
        .bodyToMono(HechoResponseDTO.class) //Puede cambiar, depende como ser la respuesta de la API
        .map(HechoResponseDTO::getData);
  }

  public Mono<ColeccionInputDTO> obtenerColeccionPorId(long id){
    return webClient.get()
        .uri("/colecciones/{id}/hechos", id)
        .retrieve()
        .bodyToMono(ColeccionInputDTO.class);
  }

  public Mono<List<ColeccionInputDTO>> obtenerColecciones(){
    return webClient.get()
        .uri("/colecciones")
        .retrieve()
        .bodyToMono(ColeccionResponseDTO.class)
        .map(ColeccionResponseDTO::getData);
  }

  public Mono<List<SolicitudInputDTO>> crearSolicitud(SolicitudInputDTO solicitud) {
    return webClient.post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        // suponiendo que la API responde un JSON que envuelve la lista en un "data"
        .bodyToMono(SolicitudResponseDTO.class)
        .map(SolicitudResponseDTO::getData);
  }

}*/