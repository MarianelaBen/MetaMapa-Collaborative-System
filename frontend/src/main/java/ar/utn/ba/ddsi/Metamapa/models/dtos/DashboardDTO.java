package ar.utn.ba.ddsi.Metamapa.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Long totalHechos;
    private Long hechosVerificados;
    private Long spamDetectado;
    private Double porcentajeSpam;

    private Map<String, Long> hechosPorCategoria;
    private Map<String, Long> hechosPorProvincia;
    private Map<Integer, Long> hechosPorHora;
}