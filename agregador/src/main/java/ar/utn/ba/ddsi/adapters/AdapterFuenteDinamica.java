package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputComunDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Etiqueta;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Component
public class AdapterFuenteDinamica {

    private final WebClient webClient;

    @Autowired
    public AdapterFuenteDinamica(WebClient.Builder webClientBuilder) {
      this.webClient = webClientBuilder.build();
    }

    public List<Hecho> obtenerHechos(String fuenteUrl) {
      List<HechoInputComunDTO> hechosDTO = webClient.get()
          .uri(fuenteUrl)
          .retrieve()
          .bodyToFlux(HechoInputComunDTO.class)
          .collectList()
          .block();

      if (hechosDTO == null) {return List.of();}
      return hechosDTO.stream().map(this::mapToHecho).collect(Collectors.toList());
    }

    public Hecho mapToHecho(HechoInputComunDTO dto) {
    Hecho hecho = new Hecho(
        dto.getTitulo(),
        dto.getDescripcion(),
        new Categoria(dto.getCategoria()),
        new Ubicacion(
            dto.getUbicacion() != null ? dto.getUbicacion().getLatitud() : null,
            dto.getUbicacion() != null ? dto.getUbicacion().getLongitud() : null
        ),
        dto.getFechaAcontecimiento(),
        dto.getFechaCarga(),
        Origen.PROVISTO_POR_CONTRIBUYENTE,
         null
    );

    hecho.setFueEliminado(dto.getFueEliminado());
    if (dto.getPathMultimedia() != null) {hecho.setPathMultimedia(dto.getPathMultimedia());}
    if (dto.getEtiquetas() != null) {
      for (String nombre : dto.getEtiquetas()) {hecho.agregarEtiqueta(new Etiqueta(nombre));}}
/*
    // extras.contribuyente (si viene)
    if (dto.getExtras() != null) {
      Object raw = dto.getExtras().get("contribuyente");
      if (raw instanceof Map<?, ?> m) {
        String nombre = asString(m.get("nombre"));
        String apellido = asString(m.get("apellido"));
        LocalDate fnac = parseLocalDate(m.get("fechaDeNacimiento"));
        Contribuyente c = new Contribuyente(nombre, apellido, fnac); // ajusta al constructor real
        h.setContribuyente(c);
      }
    }

    return h;
  }

  public static String asString(Object o) {
    return o == null ? null : String.valueOf(o);
  }

  public static LocalDate parseLocalDate(Object o) {
    try { return (o == null) ? null : LocalDate.parse(String.valueOf(o)); }
    catch (Exception e) { return null; }*/
      return hecho;
    }
}
