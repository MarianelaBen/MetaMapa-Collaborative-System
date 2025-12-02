package ar.utn.ba.ddsi.models.dtos.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CriterioInputDTO {
    private String tipoCriterio; // "TITULO", "CATEGORIA", "LUGAR", etc.
    private String valorString;  // Para titulo, descripcion, origen, nombre categoria

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDesde;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaHasta;

    // Lugar
    private Double latitud;
    private Double longitud;
    private Integer rango;
}