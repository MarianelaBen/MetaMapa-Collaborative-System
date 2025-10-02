package ar.utn.ba.ddsi.Metamapa.dtos;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class SolicitudDto {

    private Long id;
    private String estado;
    private String justificacion;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaAtencion;
}
