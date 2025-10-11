package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.CriterioDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ColeccionService {
    private final WebClient webClient;
    private final WebClient webClientPublic;

    public ColeccionService(WebClient.Builder webClientBuilder,
                            @Value("${backend.api.base-url}") String baseUrl,
                            @Value("${backend.api.base-url-agregador}") String baseUrlPublic) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.webClientPublic = webClientBuilder
                .baseUrl(baseUrlPublic)
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

    public ColeccionDTO crearColeccion(ColeccionDTO coleccion) {
        return webClient.post()
            .uri("/colecciones")
            .bodyValue(coleccion)
            .retrieve()
            .bodyToMono(ColeccionDTO.class)
            .block();
    }

    public void eliminarColeccion(String handle){
        try {
            webClient
                    .delete()
                    .uri("/colecciones/{handle}", handle)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al eliminar coleccion: " + e.getResponseBodyAsString(), e);
        }
    }

    public void sumarVistaColeccion(String handle) {
        try {
            webClientPublic.post()
                    .uri("/coleccion/{handle}/vista", handle)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        }catch (WebClientResponseException e){
            throw new RuntimeException("Error al sumar vista a la coleccion: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<ColeccionDTO> traerColeccionesDestacadas(){
        try {
            return webClientPublic.get()
                    .uri("/colecciones-destacadas")
                    .retrieve()
                    .bodyToFlux(ColeccionDTO.class)
                    .collectList()
                    .block();

        }catch (WebClientResponseException e){
            throw new RuntimeException("Error al traer colecciones: " + e.getResponseBodyAsString(), e);
        }
    }

    public ColeccionDTO actualizarColeccion(String handle, ColeccionDTO dto) {
        try {
            return webClient.put()
                .uri("/colecciones/{handle}", handle)   // mapea al AdminController del backend
                .bodyValue(dto)                          // dto con titulo, descripcion, algoritmoDeConsenso, criterioIds, etc.
                .retrieve()
                .bodyToMono(ColeccionDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al actualizar la colección: " + e.getResponseBodyAsString(), e);
        }
    }

}
