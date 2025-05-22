package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColeccionService implements IColeccionService {
  private List<Coleccion> colecciones;
  //private final long unaHora = 3600000;

  @Autowired
  private IColeccionRepository coleccionRepository;

  @Override
  public Coleccion crearColeccion(Coleccion coleccion){
    this.filtrarHechos(coleccion);

    return coleccionRepository.save(coleccion);

  }


  public Coleccion filtrarHechos(Coleccion coleccion){
    coleccion.getHechosDeLaColeccion().clear();
    //TODO aca se podria llamar al service del aagregador -> reutilizamos logica del agregador y desacoplamos logica pesada del service de colecciones
    //List<Hecho> hechos = agregador.obtenerHechosDeFuentes(coleccion.getFuentes());
    //y aen la linea de abajo en vez de coleccio.getFuentes() le mandas directamente hechos.stream().filter(hecho -> coleccion.noFueEliminado(hecho)).. etc
    List<Hecho> hechosFiltrados = coleccion.getFuentes().stream().flatMap(fuente -> fuente.getHechos().stream()).filter(hecho -> coleccion.noFueEliminado(hecho)).collect(Collectors.toList());
      if( coleccion.getCriterios().isEmpty() ) { coleccion.agregarHechos(hechosFiltrados); }
      else { coleccion.agregarHechos(hechosFiltrados.stream().filter(coleccion::cumpleLosCriterios).collect(Collectors.toList())); }
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

  }

