package ar.utn.ba.ddsi.MetaMapa.models.entities.criterios;

import static org.junit.jupiter.api.Assertions.*;

import ar.utn.ba.ddsi.MetaMapa.models.entities.Hecho;
import ar.utn.ba.ddsi.MetaMapa.models.entities.fuentes.FuenteEstatica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuenteEstaticaTest {
  private FuenteEstatica fuente;
  private Hecho h1;

  @Test
  @DisplayName("Tests de fuenteEstatica")
  public void cargarHechos() {
    this.fuente = new FuenteEstatica("src/test/java/ar/utn/ba/ddsi/MetaMapa/desastres_naturales_argentina.csv");

    fuente.leerHechos();

    assertEquals("Ráfagas de más de 100 km/h causa estragos en San Vicente, Misiones", fuente.getHechosCargados().get(0).getTitulo());
    assertEquals(10,fuente.getHechosCargados().size());
    System.out.println(fuente.getHechosCargados().get(0).getTitulo());
    System.out.println(fuente.getHechosCargados().get(1).getFechaAcontecimiento());
    System.out.println(fuente.getHechosCargados().get(2).getFechaAcontecimiento());
    System.out.println(fuente.getHechosCargados().size());
  }
}