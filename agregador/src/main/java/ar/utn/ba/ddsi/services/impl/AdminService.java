package ar.utn.ba.ddsi.services.impl;
import ar.utn.ba.ddsi.Exceptions.ColeccionCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.*;
import ar.utn.ba.ddsi.models.dtos.output.*;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.repositories.*;
import ar.utn.ba.ddsi.models.repositories.impl.ColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.impl.FuenteRepository;
import ar.utn.ba.ddsi.models.repositories.impl.SolicitudRepository;
import ar.utn.ba.ddsi.services.IAdminService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {
  private final IColeccionRepository coleccionRepo;
  private final IColeccionService coleccionService;
  //private final ConsensoRepository consensoRepo;
  private final ISolicitudRepository solicitudRepo;

  public AdminService(ColeccionRepository coleccionRepo,
                      IFuenteRepository fuenteRepo,
                      //ConsensoRepository consensoRepo,
                      ISolicitudRepository solicitudRepo,
                      IColeccionService coleccionService) {
    this.coleccionRepo = coleccionRepo;
   // this.consensoRepo = consensoRepo;
    this.solicitudRepo = solicitudRepo;
    this.coleccionService = coleccionService;
  }

  //API ADMINISTRATIVA

  //OPERACIONES CRUD
  //Devuelve las colecciones
  @Override
  public List<ColeccionOutputDTO> getColecciones() {
    return coleccionRepo.findAll().stream()
        .map(ColeccionOutputDTO::fromEntity) //Convertimos cada entidad a DTO
        .collect(Collectors.toList());
  }

  //Crea coleccion a partir del DTO recibido

  public ColeccionOutputDTO modificarTipoAlgoritmoConsenso(TipoAlgoritmoDeConsenso tipoAlgoritmo, String id){
    var coleccion = coleccionRepo.findById(id);
    if(coleccion == null){
      throw new NoSuchElementException("No se puede modificar el Algoritmo de Consenso. Coleccion no encontrada con ID: " + id);
    }
    coleccion.setAlgoritmoDeConsenso(tipoAlgoritmo);
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(coleccion));
  }

  //Actualiza una coleccion si la encuentra por ID
  @Override
  public ColeccionOutputDTO actualizarColeccion(String id, ColeccionInputDTO dto) {
    Coleccion existing = coleccionRepo.findById(id);
    if (existing == null) {
      throw new NoSuchElementException("Coleccion no encontrada: " + id);
    }
    existing.setTitulo(dto.getTitulo());
      existing.setDescripcion(dto.getDescripcion());
      return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
  }


  //Elimina coleccion por ID
  @Override
  public void eliminarColeccion(String id) {
    var coleccion = this.coleccionRepo.findById(id);
    if(coleccion == null) {
      throw new NoSuchElementException("No se puede eliminar. Coleccion no encontrada con ID: " + id);
    }
    this.coleccionRepo.deleteById(id);
  }


  //Obtención de todos los hechos de una colección
  @Override
  public List<HechoOutputDTO> getHechos(String coleccionId) {
    var coleccion = coleccionRepo.findById(coleccionId);
    if(coleccion == null) {
      throw new NoSuchElementException("Coleccion no encontrada con ID: " + coleccionId);
    }
    return coleccion.getHechos()
        .stream()
        .map(HechoOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }


  //Agregar fuentes de hechos de una colección
  @Override
  public FuenteOutputDTO agregarFuente(String coleccionId, FuenteInputDTO dto) {
    var coleccion = coleccionRepo.findById(coleccionId);
    if (coleccion == null) {
      throw new NoSuchElementException("No se encontró la coleccion " + coleccionId);
    }
    Fuente fuente = new Fuente(dto.getUrl(), dto.getTipo());              // Convertimos el DTO a entidad
    //fuenteRepo.save(fuente);  TODO agregar linea ahora que tenemos repositorio?
    coleccion.agregarFuentes(fuente);            // Asociamos la fuente desde la colección
    coleccionRepo.save(coleccion);               // Guardamos la colección con la nueva fuente
    coleccionService.actualizarColecciones();    //TODO borrar esta linea

    return FuenteOutputDTO.fromEntity(fuente);    // Devolvemos la fuente recién agregada
  }

  //Quitar fuentes de hechos de una colección
  @Override
  public boolean eliminarFuenteDeColeccion(String coleccionId, Long fuenteId) {
    var coleccion = coleccionRepo.findById(coleccionId);

    if (coleccion == null) {
      throw new NoSuchElementException("No se encontró la coleccion " + coleccionId); // No existe la colección
    }
    //Intentamos eliminar la fuente con ese id
    boolean removed = coleccion.getFuentes().removeIf(f -> {
      Long id = f.getId();
      return id != null && id.equals(fuenteId);
    }); //TODO creo que ahora podriamos usar el repositorio de fuentes

    if (removed) {
      coleccionRepo.save(coleccion);
    }
    return removed;
  }


  //Aprobar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    var s = solicitudRepo.findById(id);
    if(s == null) {
      throw new NoSuchElementException("No se encontró la solicitud " + id);
    }
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  //Denegar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    var s = solicitudRepo.findById(id);
    if(s == null) {
      throw new NoSuchElementException("No se encontró la solicitud " + id);
    }
    s.setEstado(EstadoSolicitud.RECHAZADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }
}

  /*
  @Override
  public ConsensoResponseDTO configurarConsenso(Long coleccionId, ConsensoDTO dto) {
    AlgoritmoConsenso c = consensoRepo.findByColeccionId(coleccionId)
        .orElse(new AlgoritmoConsenso());
    coleccionRepo.findById(coleccionId).ifPresent(c::setColeccion);
    c.setAlgoritmo(dto.getAlgoritmo());
    return ConsensoResponseDTO.fromEntity(consensoRepo.save(c));
  }

*/
