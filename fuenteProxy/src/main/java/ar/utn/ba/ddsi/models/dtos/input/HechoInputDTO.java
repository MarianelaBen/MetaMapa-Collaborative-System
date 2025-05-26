package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import lombok.Getter;
import java.time.LocalDate;


@Getter
@Data
public class HechoDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;

/*
public Hecho toHecho(){
  return new Hecho(
      this.titulo,
      this.descripcion,
      new Categoria(this.categoria),
      new Ubicacion(this.latitud, this.longitud),
      this.fechaAcontecimiento,
      Origen.PROVENIENTE_DE_DATASET
  );
}*/

}



