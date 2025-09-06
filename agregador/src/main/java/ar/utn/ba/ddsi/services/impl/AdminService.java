package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.Exceptions.ColeccionCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.FuenteOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAdminService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

  private final IColeccionRepository coleccionRepo;
  private final IFuenteRepository fuenteRepo;
  private final ISolicitudRepository solicitudRepo;
  private final IColeccionService coleccionService;

  public AdminService(IColeccionRepository coleccionRepo,
                      IFuenteRepository fuenteRepo,
                      ISolicitudRepository solicitudRepo,
                      IColeccionService coleccionService) {
    this.coleccionRepo = coleccionRepo;
    this.fuenteRepo = fuenteRepo;
    this.solicitudRepo = solicitudRepo;
    this.coleccionService = coleccionService;
  }

  @Override
  public List<ColeccionOutputDTO> getColecciones() {
    return coleccionRepo.findAll().stream()
        .map(ColeccionOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public ColeccionOutputDTO modificarTipoAlgoritmoConsenso(TipoAlgoritmoDeConsenso tipoAlgoritmo, String id) {
    Coleccion coleccion = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se puede modificar el Algoritmo de Consenso. Coleccion no encontrada con ID: " + id));
    coleccion.setAlgoritmoDeConsenso(tipoAlgoritmo);
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(coleccion));
  }

  @Override
  public ColeccionOutputDTO actualizarColeccion(String id, ColeccionInputDTO dto) {
    Coleccion existing = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + id));
    existing.setTitulo(dto.getTitulo());
    existing.setDescripcion(dto.getDescripcion());
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
  }

  @Override
  public void eliminarColeccion(String id) {
    Coleccion coleccion = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se puede eliminar. Coleccion no encontrada con ID: " + id));
    coleccionRepo.deleteById(id);
  }

  @Override
  public List<HechoOutputDTO> getHechos(String coleccionId) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId)
        .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada con ID: " + coleccionId));
    return coleccion.getHechos().stream()
        .map(HechoOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  public FuenteOutputDTO agregarFuente(String coleccionId, FuenteInputDTO dto) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la coleccion " + coleccionId));

    Fuente fuente = new Fuente(dto.getUrl(), dto.getTipo());
    // Guardar la fuente primero (recomendado) para que tenga id y se mantenga referencialmente consistente
    if (fuenteRepo != null) {
      fuente = fuenteRepo.save(fuente);
    }
    coleccion.agregarFuentes(fuente);
    coleccionRepo.save(coleccion);

    // Si coleccionService.actualizarColecciones() es necesario, re-habilitalo.
    return FuenteOutputDTO.fromEntity(fuente);
  }

  @Override
  public boolean eliminarFuenteDeColeccion(String coleccionId, Long fuenteId) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la coleccion " + coleccionId));

    boolean removed = coleccion.getFuentes().removeIf(f -> {
      Long id = f.getId();
      return id != null && id.equals(fuenteId);
    });

    if (removed) {
      coleccionRepo.save(coleccion);
      // opcional: fuenteRepo.deleteById(fuenteId);
    }
    return removed;
  }

  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    s.setEstado(EstadoSolicitud.RECHAZADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }
}
