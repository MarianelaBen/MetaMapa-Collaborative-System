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
  private final HechoRepository hechoRepo;
  private final FuenteRepository fuenteRepo;
  private final ConsensoRepository consensoRepo;
  private final SolicitudRepository solicitudRepo;

  public AdminService(ColeccionRepository coleccionRepo,
                      HechoRepository hechoRepo,
                      FuenteRepository fuenteRepo,
                      ConsensoRepository consensoRepo,
                      SolicitudRepository solicitudRepo) {
    this.coleccionRepo = coleccionRepo;
    this.hechoRepo = hechoRepo;
    this.fuenteRepo = fuenteRepo;
    this.consensoRepo = consensoRepo;
    this.solicitudRepo = solicitudRepo;
  }

  @Override
  public List<ColeccionOutputDTO> getColecciones() {
    return coleccionRepo.findAll().stream()
        .map(ColeccionOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto) {
    Coleccion c = dto.toEntity();
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(c));
  }

  @Override
  public Optional<ColeccionOutputDTO> actualizarColeccion(Long id, ColeccionInputDTO dto) {
    return coleccionRepo.findById(id).map(existing -> {
      existing.setNombre(dto.getNombre());
      existing.setDescripcion(dto.getDescripcion());
      return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
    });
  }

  @Override
  public void eliminarColeccion(Long id) {
    coleccionRepo.deleteById(id);
  }

  @Override
  public List<HechoOutputDTO> getHechos(Long coleccionId) {
    return hechoRepo.findByColeccionId(coleccionId).stream()
        .map(HechoOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public FuenteInputDTO agregarFuente(Long coleccionId, FuenteInputDTO dto) {
    Fuente fuente = dto.toEntity();
    coleccionRepo.findById(coleccionId).ifPresent(fuente::setColeccion);
    return FuenteInputDTO.fromEntity(fuenteRepo.save(fuente));
  }

  @Override
  public void eliminarFuente(Long fuenteId) {
    fuenteRepo.deleteById(fuenteId);
  }

  @Override
  public List<SolicitudOutputDTO> getSolicitudes(String estado) {
    return solicitudRepo.findByEstado(estado).stream()
        .map(SolicitudOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    Solicitud s = solicitudRepo.findById(id).orElseThrow();
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    Solicitud s = solicitudRepo.findById(id).orElseThrow();
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
