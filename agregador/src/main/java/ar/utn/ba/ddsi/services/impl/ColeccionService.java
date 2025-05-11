package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IColeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColeccionService implements IColeccion {
  private List<Coleccion> colecciones;

  @Autowired
  private IColeccionRepository coleccionRepository;

  @Override
  public void actualizarColecciones(){
    //TODO implementar que sea cada una hora
    List<Hecho> coleccionesActualizadas = colecciones.stream()
        .flatMap(c -> c.actualizar().stream())
        .collect((Collectors.toList()));
    //TODO actualizar en el repositorio
  }

  //findByHandle(handle);

  }

