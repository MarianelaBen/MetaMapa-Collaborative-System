package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.entities.Coleccion;

public interface IColeccionService {
  public void actualizarColecciones();
  public Coleccion crearColeccion(Coleccion coleccion);
}
