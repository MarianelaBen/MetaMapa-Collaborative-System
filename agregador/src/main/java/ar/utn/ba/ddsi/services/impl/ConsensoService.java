package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsensoService implements IConsensoService {
  @Autowired
  private IColeccionRepository coleccionRepository;

  @Override
  public void aplicarAlgoritmoDeConsenso() {
    List<Coleccion> colecciones = coleccionRepository.findAll();


    //colecciones.forEach(coleccion -> coleccion.);

    colecciones.forEach(coleccion -> coleccionRepository.save(coleccion));
  }
}
