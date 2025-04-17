package domain;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import domain.enumerados.Origen;
import domain.fuentes.Fuente;
import domain.fuentes.FuenteEstatica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

class ColeccionTest {


  Hecho h1 = new Hecho(
      "Caída de aeronave impacta en Olavarría",
      "Grave caída de aeronave ocurrió en las inmediaciones de " +
                "Olavarría, Buenos Aires. El incidente provocó pánico entre " +
                "los residentes locales. Voluntarios de diversas organizaciones " +
                "se han sumado a las tareas de auxilio.",
      "Caída de aeronave",
      -3686837,
      -60343297,
      LocalDate.of(2001, 11, 29),
      Origen.CARGA_MANUAL
  );

  @Test
  @DisplayName("Creacion de una coleccion de manera manual")
  public void crearColeccion(){
    FuenteEstatica fuente = new FuenteEstatica("ruta.csv");
    fuente.cargarHecho(h1);

    Coleccion coleccion = new Coleccion("Coleccion prueba", "Esto es una prueba", fuente);

    

  }
}