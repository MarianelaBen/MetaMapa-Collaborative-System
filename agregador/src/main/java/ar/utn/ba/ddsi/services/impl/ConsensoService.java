package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsensoService implements IConsensoService {
  @Autowired
  private IAgregadorService agregadorService;
  @Autowired
  private IColeccionRepository coleccionRepository;

  @Override
  public void aplicarAlgoritmoDeConsenso() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    List<Hecho> hechos = agregadorService.obtenerTodosLosHechos();

    colecciones.stream().filter(coleccion -> coleccion.getAlgoritmoDeConsenso() != null).forEach(coleccion -> this.usoDeAlgoritmo(coleccion));

    colecciones.forEach(coleccion -> coleccionRepository.save(coleccion));
  }

  @Override
  public void usoDeAlgoritmo(Coleccion coleccion){
    coleccion.getAlgoritmoDeConsenso();
  }
}