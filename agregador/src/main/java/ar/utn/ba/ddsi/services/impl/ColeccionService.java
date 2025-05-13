package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColeccionService implements IColeccionService {
  private List<Coleccion> colecciones;
  //private final long unaHora = 3600000;

  @Autowired
  private IColeccionRepository coleccionRepository;

  //@Scheduled(fixedRate = unaHora)
  @Override
  public void actualizarColecciones(){
    //TODO implementar que sea cada una hora
    this.colecciones = coleccionRepository.findAll();
    for (Coleccion coleccion : colecciones){
      coleccion.filtrarHechos();
      coleccionRepository.save(coleccion);
    }
  }

  }

