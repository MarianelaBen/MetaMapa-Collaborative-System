package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class HechoOutputDTO {
    private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private Set<Long> idEtiquetas;
  private List<String> idContenidoMultimedia;
  private String fuenteExterna;
  private Contribuyente contribuyente;

    public static HechoOutputDTO fromEntity(Hecho h) {
        HechoOutputDTO dto = new HechoOutputDTO();
        dto.setId(h.getId());
        dto.setTitulo(h.getTitulo());
        dto.setDescripcion(h.getDescripcion());
        dto.setCategoria(h.getCategoria().getNombre());
        dto.setLatitud(h.getUbicacion().getLatitud());
        dto.setLongitud(h.getUbicacion().getLongitud());
        dto.setFechaAcontecimiento(h.getFechaAcontecimiento());
        dto.setFechaCarga(h.getFechaCarga());
        dto.setIdEtiquetas(h.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
        dto.setIdContenidoMultimedia(h.getPathMultimedia());
        dto.setFuenteExterna(h.getFuenteExterna());
        return dto;
    }

}