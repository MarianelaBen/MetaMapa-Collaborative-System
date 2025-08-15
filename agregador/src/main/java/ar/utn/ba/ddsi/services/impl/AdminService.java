package ar.utn.ba.ddsi.services.impl;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {
  private final ColeccionRepository coleccionRepo;
  private final IColeccionService coleccionService;
  private final FuenteRepository fuenteRepo;
  //private final ConsensoRepository consensoRepo;
  private final SolicitudRepository solicitudRepo;

  public AdminService(ColeccionRepository coleccionRepo,
                      FuenteRepository fuenteRepo,
                      //ConsensoRepository consensoRepo,
                      SolicitudRepository solicitudRepo,
                      IColeccionService coleccionService) {
    this.coleccionRepo = coleccionRepo;
    this.fuenteRepo = fuenteRepo;
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

  public Coleccion modificarTipoAlgoritmoConsenso(TipoAlgoritmoDeConsenso tipoAlgoritmo, String id){
    Coleccion coleccion = coleccionRepo.findById(id);
    coleccion.setAlgoritmoDeConsenso(tipoAlgoritmo);
    return coleccion;
  }

  @Override
  public ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto) {
    Set<Fuente> fuentes = dto.getFuenteIds().stream().map(fuenteRepo::findById).collect(Collectors.toSet());
    Coleccion c = new Coleccion(dto.getTitulo(), dto.getDescripcion(), fuentes);  //Convertimos DTO a entidad
    c.setHandle(dto.getHandle());
    c.setAlgoritmoDeConsenso(dto.getAlgoritmoDeConsenso());
    //return ColeccionOutputDTO.fromEntity(coleccionRepo.save(c));  // Guardamos y devolvemos el DTO de salida
    return ColeccionOutputDTO.fromEntity(coleccionService.crearColeccion(c));
  }



  //Actualiza una coleccion si la encuentra por ID
  @Override
  public ColeccionOutputDTO actualizarColeccion(String id, ColeccionInputDTO dto) {
    Coleccion existing = coleccionRepo.findById(id);
      existing.setTitulo(dto.getTitulo());
      existing.setDescripcion(dto.getDescripcion());
      return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
  }


  //Elimina coleccion por ID
  @Override
  public void eliminarColeccion(String id) {
    coleccionRepo.deleteById(id);
    //TODO verificar que exista
  }


  //Obtención de todos los hechos de una colección
  @Override
  public List<HechoOutputDTO> getHechos(String coleccionId) {
    return coleccionRepo.findById(coleccionId).getHechos()
        .stream()
        .map(HechoOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }


  //Agregar fuentes de hechos de una colección
  @Override
  public FuenteInputDTO agregarFuente(String coleccionId, FuenteInputDTO dto) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId);
    if (coleccion == null) {
      throw new RuntimeException("No se encontró la colección " + coleccionId);
    }
    Fuente fuente = new Fuente(dto.getUrl(), dto.getTipo());              // Convertimos el DTO a entidad
    coleccion.agregarFuentes(fuente);            // Asociamos la fuente desde la colección
    coleccionRepo.save(coleccion);               // Guardamos la colección con la nueva fuente
    coleccionService.actualizarColecciones();    //TODO borrar esta linea

    return FuenteInputDTO.fromEntity(fuente);    // Devolvemos la fuente recién agregada
  }

  //Quitar fuentes de hechos de una colección
  @Override
  public boolean eliminarFuenteDeColeccion(String coleccionId, Long fuenteId) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId);

    if (coleccion == null) {
      return false; // No existe la colección
    }

    //Intentamos eliminar la fuente con ese id
    boolean removed = coleccion.getFuentes().removeIf(f -> {
      Long id = f.getId();
      return id != null && id.equals(fuenteId);
    });

    if (removed) {
      coleccionRepo.save(coleccion);
    }
    return removed;
  }


  //Aprobar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id);
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  //Denegar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id); //si no existe, excepcion
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
