/*
package ar.utn.ba.ddsi.services.impl;


import ar.utn.ba.ddsi.models.dtos.input.*;
import ar.utn.ba.ddsi.models.dtos.output.*;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.*;
import ar.utn.ba.ddsi.models.repositories.impl.ColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.impl.FuenteRepository;
import ar.utn.ba.ddsi.models.repositories.impl.SolicitudRepository;
import ar.utn.ba.ddsi.services.IAdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {
  private final ColeccionRepository coleccionRepo;
  private final FuenteRepository fuenteRepo;
  private final ConsensoRepository consensoRepo;
  private final SolicitudRepository solicitudRepo;

  public AdminService(ColeccionRepository coleccionRepo,
                      FuenteRepository fuenteRepo,
                      ConsensoRepository consensoRepo,
                      SolicitudRepository solicitudRepo) {
    this.coleccionRepo = coleccionRepo;
    this.fuenteRepo = fuenteRepo;
    this.consensoRepo = consensoRepo;
    this.solicitudRepo = solicitudRepo;
  }


 //Devuelve las colecciones
  @Override
  public List<ColeccionOutputDTO> getColecciones() {
    return coleccionRepo.findAll().stream()
        .map(ColeccionOutputDTO::fromEntity) //Convertimos cada entidad a DTO
        .collect(Collectors.toList());
  }

 //Crea coleccion a partir del DTO recibido
  @Override
  public ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto) {
    Coleccion c = dto.toEntity(); //Convertimos DTO a entidad
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(c));  // Guardamos y devolvemos el DTO de salida
  }


 //Actualiza una coleccion si la encuentra por ID
  @Override
  public Optional<ColeccionOutputDTO> actualizarColeccion(Long id, ColeccionInputDTO dto) {
    return coleccionRepo.findById(id).map(existing -> {
      existing.setNombre(dto.getNombre());
      existing.setDescripcion(dto.getDescripcion());
      return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
    });
  }


 //Elimina coleccion por ID
  @Override
  public void eliminarColeccion(Long id) {
    coleccionRepo.deleteById(id);
    //TODO verificar que exista
  }


 //Obtención de todos los hechos de una colección
  @Override
  public List<HechoOutputDTO> getHechos(Long coleccionId) {
    return coleccionRepo.findById(coleccionId)
      .map(Coleccion::getHechos) // hechos de la coleccion
     .orElse(List.of()) // si no existe , lista vacia
     .stream()
     .map(HechoOutputDTO::fromEntity) //Convertimos a DTO
     .collect(Collectors.toList());
  }


 //Agregar fuentes de hechos de una colección
  @Override
  public FuenteInputDTO agregarFuente(Long coleccionId, FuenteInputDTO dto) {
    Fuente fuente = dto.toEntity();
    coleccionRepo.findById(coleccionId).ifPresent(fuente::setColeccion);
    return FuenteInputDTO.fromEntity(fuenteRepo.save(fuente));
  }

 //Quitar fuentes de hechos de una colección
  @Override
  public void eliminarFuenteDeColeccion(Long coleccionId, Long fuenteId) {
    // Quitamos la fuente con el id que corresponda de la coleccion
    coleccionRepo.findById(coleccionId).ifPresent(coleccion -> {
      coleccion.getFuentes().removeIf(f -> f.getId().equals(fuenteId));
      coleccionRepo.save(coleccion);
    });
  }

 //Devuelve lista de solicitudes y pueden ser filtradas por estado
  @Override
  public List<SolicitudOutputDTO> getSolicitudes(String estado) {
    return solicitudRepo.findByEstado(estado).stream()
        .map(SolicitudOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }


 //Aprobar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    Solicitud s = solicitudRepo.findById(id).orElseThrow();
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  //Denegar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    Solicitud s = solicitudRepo.findById(id).orElseThrow(); //si no existe, excepcion
    s.setEstado(EstadoSolicitud.RECHAZADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
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


  @Override
  public Optional<ConsensoResponseDTO> getConsenso(Long coleccionId) {
    return consensoRepo.findByColeccionId(coleccionId)
        .map(ConsensoResponseDTO::fromEntity);
  }
}*/
