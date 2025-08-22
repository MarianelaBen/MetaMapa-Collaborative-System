package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.Exceptions.ColeccionCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.models.repositories.impl.FuenteRepository;
import ar.utn.ba.ddsi.modosDeNavegacion.IModoDeNavegacion;
import ar.utn.ba.ddsi.modosDeNavegacion.impl.ModoDeNavegacionFactory;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ColeccionService implements IColeccionService {
  private List<Coleccion> colecciones;

  @Autowired
  private ModoDeNavegacionFactory modoDeNavegacionFactory;
  @Autowired
  private IColeccionRepository coleccionRepository;
  @Autowired
  private @Lazy IAgregadorService agregadorService;
  @Autowired
  private IFuenteRepository fuenteRepo;


  @Override
  public Coleccion crearColeccion(Coleccion coleccion){
    if (!coleccion.getFuentes().isEmpty()){ this.filtrarHechos(coleccion); }

    return coleccionRepository.save(coleccion);
  }

  @Override
  public ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto) {
    try {
      Set<Fuente> fuentes = dto.getFuenteIds()
          .stream()
          .map(fuenteId -> fuenteRepo.findById(fuenteId))
          .collect(Collectors.toSet());

      Coleccion c = new Coleccion(dto.getTitulo(), dto.getDescripcion(), fuentes);
      c.setHandle(dto.getHandle());
      c.setAlgoritmoDeConsenso(dto.getAlgoritmoDeConsenso());

      return ColeccionOutputDTO.fromEntity(this.crearColeccion(c));
    } catch (Exception e) {
      throw new ColeccionCreacionException("Error al crear la coleccion: " + e.getMessage());
    }
  }


  public Coleccion findById(String id){
    return coleccionRepository.findById(id);
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
    /*colecciones = coleccionRepository.findAll();
    for (Coleccion coleccion : colecciones){
      this.filtrarHechos(coleccion);
      coleccionRepository.save(coleccion);
    }*/ //TODO COMPROBAR SI SE PUEDE BORRAR
    List<Coleccion> coleccionesExistentes = new ArrayList<>(coleccionRepository.findAll());
    for (Coleccion coleccion : coleccionesExistentes) {
      filtrarHechos(coleccion);
      coleccionRepository.save(coleccion);
    }
  }

  @Override
  public List<Hecho> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modoNavegacion) {
    Coleccion coleccion = coleccionRepository.findById(coleccionId);
        //.orElseThrow(() -> new RuntimeException("No se encontro la coleccion"));

    List<Hecho> hechos = coleccion.getHechos().stream()
        .filter(h -> !h.isFueEliminado())
        .collect(Collectors.toList());

    IModoDeNavegacion modo = modoDeNavegacionFactory.resolver(modoNavegacion);

    return modo.aplicarModo(hechos, coleccion.getAlgoritmoDeConsenso());
  }

  }

