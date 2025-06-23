package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ColeccionService implements IColeccionService {
  private List<Coleccion> colecciones;

  @Autowired
  private IColeccionRepository coleccionRepository;
  private IAgregadorService agregadorService;
  @Override
  public Coleccion crearColeccion(Coleccion coleccion){
    this.filtrarHechos(coleccion);

    return coleccionRepository.save(coleccion);

  }


  public Coleccion filtrarHechos(Coleccion coleccion){
    coleccion.getHechos().clear();
    List<Hecho> hechosFiltrados = agregadorService.obtenerTodosLosHechos(coleccion.getFuentes())
        .stream()
        .filter(hecho -> coleccion.noFueEliminado(hecho))
        .collect(Collectors.toList());
      if( coleccion.getCriterios().isEmpty() ) { coleccion.agregarHechos(hechosFiltrados); }
      else { coleccion.agregarHechos(hechosFiltrados.stream()
          .filter(coleccion::cumpleLosCriterios)
          .collect(Collectors.toList())); }
    return coleccion;
  }

  @Override
  public void actualizarColecciones(){
    colecciones = coleccionRepository.findAll();
    for (Coleccion coleccion : colecciones){
      this.filtrarHechos(coleccion);
      coleccionRepository.save(coleccion);
    }
  }

  @Override
  public List<Hecho> obtenerHechos() {
    return coleccionRepository.findAll().stream()
        .flatMap(c -> c.getHechos().stream())
        .filter(h -> !h.isFueEliminado())
        .collect(Collectors.toList());
  }

  }

