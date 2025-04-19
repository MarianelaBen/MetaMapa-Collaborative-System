package domain;

import static org.junit.jupiter.api.Assertions.*;

import domain.criterios.CriterioCategoria;
import domain.criterios.CriterioFechaAcontecimiento;
import domain.criterios.CriterioTitulo;
import domain.fuentes.FuenteEstatica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

class FuenteEstaticaTest {
  private FuenteEstatica fuente;
  private Hecho h1;

  @Test
  @DisplayName("Tests de fuenteEstatica")
  public void cargarHechos() {
    this.fuente = new FuenteEstatica("src/test/java/domain/desastres_naturales_argentina.csv");

    fuente.leerHechos();

    assertEquals("Ráfagas de más de 100 km/h causa estragos en San Vicente, Misiones",fuente.hechosCargados.get(0).titulo);
    assertEquals(10,fuente.hechosCargados.size());
    System.out.println(fuente.hechosCargados.get(0).titulo);
    System.out.println(fuente.hechosCargados.get(1).fechaAcontecimiento);
    System.out.println(fuente.hechosCargados.get(2).fechaAcontecimiento);
    System.out.println(fuente.hechosCargados.size());
  }
}