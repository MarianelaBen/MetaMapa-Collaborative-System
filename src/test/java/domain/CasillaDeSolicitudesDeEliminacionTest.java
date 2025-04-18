package domain;

import static org.junit.jupiter.api.Assertions.*;

import domain.enumerados.Origen;
import domain.fuentes.FuenteEstatica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

class CasillaDeSolicitudesDeEliminacionTest {
  private Hecho h6;
  private Solicitud solicitud;
  private CasillaDeSolicitudesDeEliminacion casilla;
  private Administrador administrador;
  private FuenteEstatica fuente;

  @Test
  @DisplayName("Tests de solicitud de eliminación")
  public void eliminarHecho(){

    this.h6 = new Hecho(
        "Brote de enfermedad contagiosa causa estragos en San Lorenzo, Santa Fe",
        "Grave brote de enfermedad contagiosa ocurrió en las inmediaciones " +
            "de San Lorenzo, Santa Fe. El incidente dejó varios heridos y daños materiales. " +
            "Se ha declarado estado de emergencia en la región para facilitar la asistencia.",
        "Desastre Tecnológico - Evento sanitario",
        -32786098,
        -60741543,
        LocalDate.of(2005, 7, 5),
        Origen.CARGA_MANUAL
    );

    Solicitud solicitud = new Solicitud(h6, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
        "Phasellus varius lorem eget congue auctor. Maecenas sapien nibh, condimentum non augue non, pretium " +
        "viverra arcu. Nam eget erat at est pretium facilisis. Duis sollicitudin neque gravida, maximus felis " +
        "in, condimentum arcu. Curabitur tempus volutpat nunc eget fringilla. Donec leo tellus, pretium id bibendum " +
        "id, commodo volutpat dui. Maecenas volutpat erat sed libero lobortis, at pulvinar risus venenatis. Proin " +
        "non sem eleifend orci aliquam.");

    CasillaDeSolicitudesDeEliminacion casilla = new CasillaDeSolicitudesDeEliminacion();

    Administrador administrador = new Administrador(casilla);
    this.fuente = new FuenteEstatica("ruta.csv");
    fuente.cargarHechos(h6);

    Coleccion coleccion = new Coleccion("Coleccion prueba", "Esto es una prueba", fuente);

    coleccion.filtrarHechos();

    casilla.recibirSolicitud(solicitud);

    administrador.rechazarSolicitud();

    coleccion.filtrarHechos();

    assertFalse(casilla.solicitudesPendientes.contains(solicitud));
    assertTrue(casilla.solicitudesAtendidas.contains(solicitud));
    assertTrue(coleccion.getHechosDeLaColeccion().contains(h6));
  }

}