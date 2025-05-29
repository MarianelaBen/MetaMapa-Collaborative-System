package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public interface IColeccionService {
  public void actualizarColecciones();
  public Coleccion crearColeccion(Coleccion coleccion);
  public List<Hecho> obtenerHechosDeColeccion();
}
