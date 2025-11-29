package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoProxyInputDTO; // <--- Importamos el nuevo DTO
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdapterFuenteProxy {

    private final WebClient webClient;

    @Autowired
    public AdapterFuenteProxy(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<Hecho> obtenerHechos(String fuenteUrl) {
        List<HechoProxyInputDTO> hechosDTO = webClient.get()
                .uri(fuenteUrl)
                .retrieve()
                .bodyToFlux(HechoProxyInputDTO.class)
                .collectList()
                .block();

        if (hechosDTO == null) return List.of();

        return hechosDTO.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    private Hecho mapToEntity(HechoProxyInputDTO dto) {
        Hecho hecho = new Hecho();

        hecho.setTitulo(dto.getTitulo());
        hecho.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : "Sin descripción");
        if (dto.getCategoria() != null) {
            hecho.setCategoria(new Categoria(dto.getCategoria()));
        } else {
            hecho.setCategoria(new Categoria("Sin categoría"));
        }

        if (dto.getFechaAcontecimiento() != null) {
            hecho.setFechaAcontecimiento(dto.getFechaAcontecimiento().atStartOfDay());
        } else {
            hecho.setFechaAcontecimiento(LocalDateTime.now());
        }

        hecho.setOrigen(Origen.PROXY);

        hecho.setFechaCarga(java.time.LocalDate.now());
        hecho.setFueEliminado(false);

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(dto.getLatitud());
        ubicacion.setLongitud(dto.getLongitud());
        ubicacion.setProvincia(dto.getProvincia() != null ? dto.getProvincia() : "Ubicación Externa");
        hecho.setUbicacion(ubicacion);

        return hecho;
    }
}