package domain;

import static org.junit.jupiter.api.Assertions.*;

import domain.enumerados.EstadoSolicitud;
import domain.enumerados.Origen;
import domain.fuentes.FuenteEstatica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class CasillaDeSolicitudesDeEliminacionTest {
  private Hecho h6;
  private Solicitud solicitud1;
  private Solicitud solicitud2;
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
        new Categoria("Desastre Tecnológico - Evento sanitario"),
        new Ubicacion(-32.786098, -60.741543),
        LocalDate.of(2005, 7, 5),
        Origen.CARGA_MANUAL
    );



    Solicitud solicitud1 = new Solicitud(h6, "QvzBhELgRnyJxAUcpMkTFeiWdsaYoNqKhmCltVrbPZjfXuwOGDEyHNmvqLiKRCxTJgSpnbuzYAlwMfBdXQJeVRstkwNypZxgiUoLDaFbMHErjKnCVUgzqlBfOXehcsAMRWnJduKYIvTxpNZGqLromphXbVECtwUDzYnkgfSaMJqiLBorNXcuPtvmWGFzdkHljQEaRTBSHyCMOvUdFPKnxyrzGHqiaWcTEbJYLMvoZDwfKtpbnrsXmgUOeVhRCyqAlWtJKzgfNPdvhmeTuSWRaiLkMXnOYqZcXJbPlgfTQvzBhELgRnyJxAUcpMkTFeiWdsaYoNqKhmCltVrbPZjfXuwOGDEyHNmvqLiKRCxTJgSpnbuzYAlwMfBdXQJeVRstkwNypZxgiUoLDaFbMHErjKnCVUgzqlBfOXehcsAMRWnJduKYIvTxpNZGqLromphXbVECtwUDzYnkgfSaMJqiLBorNXcuPtvmWGFzdkHljQEaRTBSHyCM");

    casilla = CasillaDeSolicitudesDeEliminacion.getInstancia();

    Administrador administrador = new Administrador("Tomás", "Sagrada");
    this.fuente = new FuenteEstatica("ruta.csv");
    fuente.cargarHechos(h6);

    Coleccion coleccion = new Coleccion("Coleccion prueba", "Esto es una prueba", fuente);

    coleccion.filtrarHechos();

    casilla.recibirSolicitud(solicitud1);
    casilla.getSolicitudesPendientes().remove(0);

    solicitud1.setAdministradorQueAtendio(administrador);
    solicitud1.setFechaAtencion(LocalDateTime.now());
    casilla.rechazar(solicitud1);

    coleccion.filtrarHechos();

    assertFalse(casilla.getSolicitudesPendientes().contains(solicitud1));
    assertTrue(casilla.getSolicitudesAtendidas().contains(solicitud1));
    assertSame(EstadoSolicitud.RECHAZADA, solicitud1.getEstado());
    assertTrue(coleccion.getHechosDeLaColeccion().contains(h6));

    Solicitud solicitud2 = new Solicitud(h6, "QvzBhELgRnyJxAUcpMkTFeiWdsaYoNqKhmCltVrbPZjfXuwOGDEyHNmvqLiKRCxTJgSpnbuzYAlwMfBdXQJeVRstkwNypZxgiUoLDaFbMHErjKnCVUgzqlBfOXehcsAMRWnJduKYIvTxpNZGqLromphXbVECtwUDzYnkgfSaMJqiLBorNXcuPtvmWGFzdkHljQEaRTBSHyCMOvUdFPKnxyrzGHqiaWcTEbJYLMvoZDwfKtpbnrsXmgUOeVhRCyqAlWtJKzgfNPdvhmeTuSWRaiLkMXnOYqZcXJbPlgfTQvzBhELgRnyJxAUcpMkTFeiWdsaYoNqKhmCltVrbPZjfXuwOGDEyHNmvqLiKRCxTJgSpnbuzYAlwMfBdXQJeVRstkwNypZxgiUoLDaFbMHErjKnCVUgzqlBfOXehcsAMRWnJduKYIvTxpNZGqLromphXbVECtwUDzYnkgfSaMJqiLBorNXcuPtvmWGFzdkHljQEaRTBSHyCM");

    casilla.recibirSolicitud(solicitud2);
    casilla.getSolicitudesPendientes().remove(0);

    solicitud2.setAdministradorQueAtendio(administrador);
    solicitud2.setFechaAtencion(LocalDateTime.now());
    casilla.aceptar(solicitud2);

    coleccion.filtrarHechos();

    assertFalse(casilla.getSolicitudesPendientes().contains(solicitud2));
    assertTrue(casilla.getSolicitudesAtendidas().contains(solicitud2));
    assertSame(EstadoSolicitud.ACEPTADA, solicitud2.getEstado());
    assertFalse(coleccion.getHechosDeLaColeccion().contains(h6));
  }

}