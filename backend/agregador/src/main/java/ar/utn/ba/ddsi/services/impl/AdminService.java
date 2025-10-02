package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.Exceptions.ColeccionCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.*;
import ar.utn.ba.ddsi.models.dtos.output.*;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAdminService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

  private final IColeccionRepository coleccionRepo;
  private final IFuenteRepository fuenteRepo;
  private final ISolicitudRepository solicitudRepo;
  private final IColeccionService coleccionService;
  private final IHechoRepository hechoRepo;

  public AdminService(IColeccionRepository coleccionRepo,
                      IFuenteRepository fuenteRepo,
                      //ConsensoRepository consensoRepo,
                      ISolicitudRepository solicitudRepo,
                      IColeccionService coleccionService,
                      IHechoRepository hechoRepo) {
    this.coleccionRepo = coleccionRepo;
   // this.consensoRepo = consensoRepo;
    this.solicitudRepo = solicitudRepo;
    this.coleccionService = coleccionService;
    this.fuenteRepo = fuenteRepo;
    this.hechoRepo = hechoRepo;
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
    @Override
    public ColeccionOutputDTO getColeccionByHandle(String handle) {
        var coleccion = coleccionRepo.findByHandle(handle)
                .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada con handle: " + handle));
        return ColeccionOutputDTO.fromEntity(coleccion);
    }


    @Override
  public ColeccionOutputDTO modificarTipoAlgoritmoConsenso(TipoAlgoritmoDeConsenso tipoAlgoritmo, String id) {
    Coleccion coleccion = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se puede modificar el Algoritmo de Consenso. Coleccion no encontrada con ID: " + id));
    coleccion.setAlgoritmoDeConsenso(tipoAlgoritmo);
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(coleccion));
  }

  //Actualiza una coleccion si la encuentra por ID
  @Override
  public ColeccionOutputDTO actualizarColeccion(String id, ColeccionInputDTO dto) {
    Coleccion existing = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + id));
    existing.setTitulo(dto.getTitulo());
    existing.setDescripcion(dto.getDescripcion());
    return ColeccionOutputDTO.fromEntity(coleccionRepo.save(existing));
  }


  //Elimina coleccion por ID
  @Override
  public void eliminarColeccion(String id) {
    Coleccion coleccion = coleccionRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se puede eliminar. Coleccion no encontrada con ID: " + id));
    coleccionRepo.deleteById(id);
  }


  //Obtención de todos los hechos de una colección
  //Sirve para pruebas sin meterse en modos
  @Override
  public List<HechoOutputDTO> getHechos(String coleccionId) {
      // opcional: validar que la colección exista
      if (!coleccionRepo.existsById(coleccionId)) {
          throw new RuntimeException("Coleccion no encontrada con id: " + coleccionId);
      }

      List<Hecho> hechos = hechoRepo.findByColeccionHandle(coleccionId);

      return hechos.stream()
              .map(this::hechoOutputDTO)   // usás tu mapeador existente
              .collect(Collectors.toList());
  }


  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO hechoOutputDTO = new HechoOutputDTO();
    hechoOutputDTO.setTitulo(hecho.getTitulo());
    hechoOutputDTO.setDescripcion(hecho.getDescripcion());
    hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
    hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
    hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
    hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    if(hecho.getEtiquetas() != null){
      hechoOutputDTO.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toSet()));
    }
    if(hecho.getPathMultimedia() != null){
      hechoOutputDTO.setIdContenidoMultimedia(new ArrayList<>(hecho.getPathMultimedia()));
    }
    if(hecho.getContribuyente() != null){
      hechoOutputDTO.setContribuyente(hecho.getContribuyente());
    }
    if(hecho.getFuenteExterna() != null){
      hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    }

    return hechoOutputDTO;
  }

  //Agregar fuentes de hechos de una colección
  @Override
  public FuenteOutputDTO agregarFuente(String coleccionId, FuenteInputDTO dto) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la coleccion " + coleccionId));

    Fuente fuente = new Fuente(dto.getUrl(), dto.getTipo());
    // Guardar la fuente primero (recomendado) para que tenga id y se mantenga referencialmente consistente
    if (fuenteRepo != null) {
      fuente = fuenteRepo.save(fuente);
    }

    fuenteRepo.save(fuente);
    coleccion.agregarFuentes(fuente);            // Asociamos la fuente desde la colección
    coleccionRepo.save(coleccion);               // Guardamos la colección con la nueva fuente

    return FuenteOutputDTO.fromEntity(fuente);    // Devolvemos la fuente recién agregada
  }

  //Quitar fuentes de hechos de una colección
  @Override
  public boolean eliminarFuenteDeColeccion(String coleccionId, Long fuenteId) {
    Coleccion coleccion = coleccionRepo.findById(coleccionId)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la coleccion " + coleccionId));

    if (coleccion == null) {
      throw new NoSuchElementException("No se encontró la coleccion " + coleccionId); // No existe la colección
    }
    //Intentamos eliminar la fuente con ese id
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


  //Aprobar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO aprobarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    s.setEstado(EstadoSolicitud.ACEPTADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  //Denegar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    s.setEstado(EstadoSolicitud.RECHAZADA);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }
}
