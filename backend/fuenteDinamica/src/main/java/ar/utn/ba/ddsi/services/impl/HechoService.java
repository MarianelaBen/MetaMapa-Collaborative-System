package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.Exceptions.HechoCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ContribuyenteOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.UbicacionOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IContenidoMultimediaRepository;
import ar.utn.ba.ddsi.services.ICategoriaService;
import ar.utn.ba.ddsi.services.IContenidoMultimediaService;
import ar.utn.ba.ddsi.services.IHechoService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import ar.utn.ba.ddsi.models.entities.enumerados.Origen;

import ar.utn.ba.ddsi.models.repositories.IHechoRepository;

import java.time.temporal.ChronoUnit;

import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class HechoService implements IHechoService {

  @Autowired
  private IHechoRepository hechoRepository;
  @Autowired
  private ICategoriaRepository categoriaRepository;
  @Autowired
  private ICategoriaService categoriaService;
  @Autowired
  private IContenidoMultimediaRepository contenidoMultimediaRepository;
  @Autowired
  private IContenidoMultimediaService contenidoMultimediaService;
  @Lazy
  @Autowired
  private ISolicitudService solicitudService;
  @Autowired
  private MinioService minioService;

  @PersistenceContext
  private EntityManager em; //TODO ESTO ES SOLO PARA PRUEBAS, PORQUE NO HAY REPO DE CONTR TDV

  @Override
  @Transactional //TODO ESTO ES SOLO PARA PRUEBAS, PORQUE NO HAY REPO DE CONTR TDV
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO , MultipartFile[] multimedia) {
    // validaciones básicas
    if (hechoInputDTO.getTitulo() == null || hechoInputDTO.getTitulo().isBlank()) {
      throw new IllegalArgumentException("Título obligatorio");
    }
    if (hechoInputDTO.getDescripcion() == null || hechoInputDTO.getDescripcion().isBlank()) {
      throw new IllegalArgumentException("Descripción obligatoria");
    }
    if (hechoInputDTO.getCategoria() == null || hechoInputDTO.getCategoria().getNombre().isBlank()) {
      throw new IllegalArgumentException("Categoría obligatoria");
    }
    if (hechoInputDTO.getFechaAcontecimiento() == null) {
      throw new IllegalArgumentException("Fecha/hora del acontecimiento obligatoria");
    }
    try {
      Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());
      Ubicacion ubicacion = new Ubicacion();
      ubicacion.setProvincia(hechoInputDTO.getCiudad().getProvincia());
      ubicacion.setLatitud(hechoInputDTO.getCiudad().getLatitud());
      ubicacion.setLongitud(hechoInputDTO.getCiudad().getLongitud());

      Hecho hecho = new Hecho(
          hechoInputDTO.getTitulo(),
          hechoInputDTO.getDescripcion(),
          categoria,
          ubicacion,
          hechoInputDTO.getFechaAcontecimiento(),
          Origen.CARGA_MANUAL);


      if (multimedia != null && multimedia.length > 0) {

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : multimedia) {
          if (file != null && !file.isEmpty()) {
            String url = minioService.upload(file);
            urls.add(url);
          }
        }

        List<ContenidoMultimedia> contenidos =
            mapearMultimedia(urls);

        hecho.setContenidosMultimedia(contenidos);
      }

      // hecho.agregarEtiqueta(new Etiqueta("prueba"));  Mas adelante cambiar por DTO
      if(hechoInputDTO.getContribuyente() != null) {
        //hecho.setContribuyente(hechoInputDTO.getContribuyente());
        var contrDto = hechoInputDTO.getContribuyente(); //TODO ESTO ES SOLO PARA PRUEBAS, PORQUE NO HAY REPO DE CONTR TDV
        Contribuyente contr = new Contribuyente();
        contr.setIdContribuyente(null);
        contr.setNombre(contrDto.getNombre());
        contr.setApellido(contrDto.getApellido());
        contr.setFechaDeNacimiento(contrDto.getFechaDeNacimiento());

        em.persist(contr);
        hecho.setContribuyente(contr);
      }

      this.hechoRepository.save(hecho);
      this.solicitudService.create(hecho, TipoSolicitud.CREACION);

      return this.hechoOutputDTO(hecho);

    } catch (Exception e) {
      throw new HechoCreacionException("Error al crear el hecho: " + e.getMessage());
    }
  }

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getCategoria().getNombre());
    dto.setUbicacion(ubicacionOutputDTO(hecho.getUbicacion()));
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());
    //dto.setOrigen(hecho.getOrigen());       // extrae el id de cada etiqueta y los junta en un Set<Integer>
    // dto.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
    //dto.setContribuyente(contribuyenteOutputDTO(hecho.getContribuyente()));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode extras = mapper.createObjectNode();

    if (hecho.getFechaActualizacion() != null) {
      extras.put("fechaActualizacion", hecho.getFechaActualizacion().toString());
    }

    if (hecho.getContribuyente() != null) {
      ObjectNode c = mapper.createObjectNode();
      c.put("nombre", hecho.getContribuyente().getNombre());
      c.put("apellido", hecho.getContribuyente().getApellido());
      if (hecho.getContribuyente().getFechaDeNacimiento() != null) {
        c.put("fechaDeNacimiento", hecho.getContribuyente().getFechaDeNacimiento().toString());
      }
      extras.set("contribuyente", c);
    }

    dto.setParticulares(extras);
    dto.setEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toSet()));
    dto.setPathContenidoMultimedia(hecho.getContenidosMultimedia().stream().map(ContenidoMultimedia::getPath).collect(Collectors.toList()));
    return dto;
  }

  @Override
  public UbicacionOutputDTO ubicacionOutputDTO(Ubicacion ubicacion) {
    UbicacionOutputDTO dto = new UbicacionOutputDTO();
    dto.setLatitud(ubicacion.getLatitud());
    dto.setLongitud(ubicacion.getLongitud());
    return dto;
  }

  @Override
  public ContribuyenteOutputDTO contribuyenteOutputDTO(Contribuyente contribuyente) {
    ContribuyenteOutputDTO dto = new ContribuyenteOutputDTO();
    dto.setNombre(contribuyente.getNombre());
    dto.setApellido(contribuyente.getApellido());
    dto.setFechaDeNacimiento(contribuyente.getFechaDeNacimiento());
    return dto;
  }

  @Override
  public void eliminar(Long id) {
    var hecho = this.hechoRepository.findById(id).orElse(null);
    if (hecho == null) {
      throw new NoSuchElementException("No se puede eliminar. Hecho no encontrado con ID: " + id);
    }
      hecho.setFueEliminado(true);
      this.hechoRepository.save(hecho);
  }

  @Override
  public boolean puedeEditar(Long id1 , Long id2, LocalDate fecha) {

    boolean esMismoUsuario = Objects.equals(id1, id2);
    boolean estaDentroDelPlazo = ChronoUnit.DAYS.between(fecha, LocalDate.now()) <= 7;

    return  esMismoUsuario && estaDentroDelPlazo;
  }

  @Override
  public HechoOutputDTO permisoDeEdicion(Long idEditor, Long idHecho) {
    Hecho hecho = this.hechoRepository.findById(idHecho).orElse(null);
    if (puedeEditar(idEditor, hecho.getContribuyente().getIdContribuyente(), hecho.getFechaCarga())) {
      return this.hechoOutputDTO(hecho);
    } else {
      throw new IllegalStateException("El plazo de edicion ha expirado");
    }
  }

  /*@Override
  public HechoOutputDTO edicion(Long idEditor, HechoInputDTO hechoInputDTO, Long idHecho) {
    Hecho hecho = this.hechoRepository.findById(idHecho).orElse(null);
    HechoEstadoPrevio estadoPrevio = new HechoEstadoPrevio(hecho);

    Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());

    this.actualizarHecho(hecho,
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria, hechoInputDTO.getCiudad(),
        hechoInputDTO.getFechaAcontecimiento());

    reemplazarArchivoMultimedia(hecho, hechoInputDTO.getPathsMultimedia());

    hecho.setEstadoPrevio(estadoPrevio);

    this.solicitudService.create(hecho, TipoSolicitud.EDICION);
    this.hechoRepository.save(hecho);

    return this.hechoOutputDTO(hecho);
  }*/ //TODO borrar cuando funcione el nuevo editar

  @Override
  public HechoOutputDTO edicion(Long idEditor, HechoInputDTO dto, Long idHecho, MultipartFile[] multimedia, boolean replaceMedia, List<String> deleteExisting) {
    Hecho hecho = hechoRepository.findById(idHecho).orElseThrow(() -> new RuntimeException("Hecho no encontrado"));
    HechoEstadoPrevio estadoPrevio = new HechoEstadoPrevio(hecho);
    Categoria categoria = categoriaService.findCategory(dto.getCategoria());

    actualizarHecho(
        hecho,
        dto.getTitulo(),
        dto.getDescripcion(),
        categoria,
        dto.getCiudad(),
        dto.getFechaAcontecimiento()
    );

    actualizarMultimedia(hecho, multimedia, replaceMedia, deleteExisting);

    hecho.setEstadoPrevio(estadoPrevio);

    solicitudService.create(hecho, TipoSolicitud.EDICION);

    hechoRepository.save(hecho);

    return hechoOutputDTO(hecho);
  }


  @Override
  public void creacionRechazada(Hecho hecho){
    hecho.setFueEliminado(true);
    this.hechoRepository.save(hecho);
  }

  @Override
  public void edicionRechazada(Hecho hecho){
    HechoEstadoPrevio estadoPrevio = hecho.getEstadoPrevio();
    hecho.setEstadoPrevio(null);

    this.actualizarHecho(hecho,
        estadoPrevio.getTitulo(),
        estadoPrevio.getDescripcion(),
        estadoPrevio.getCategoria(),
        estadoPrevio.getUbicacion(),
        estadoPrevio.getFechaAcontecimiento());

    reemplazarArchivoMultimedia(hecho, hecho.getPathsMultimedia(hecho.getContenidosMultimedia()));

    this.hechoRepository.save(hecho);
  }

  private void reemplazarArchivoMultimedia(Hecho hecho, List<String> nuevosPaths) {
    if (nuevosPaths == null) return;

    List<ContenidoMultimedia> nuevoContenidoMultimedia = mapearMultimedia(nuevosPaths);

    if (hecho.getContenidosMultimedia() != null) {
      hecho.getContenidosMultimedia().forEach(c ->
          contenidoMultimediaRepository.delete(c)
      );
    }
    if (nuevoContenidoMultimedia != null) {
      hecho.setContenidosMultimedia(nuevoContenidoMultimedia);
    }
  }

  private List<ContenidoMultimedia> mapearMultimedia(List<String> paths) {
    if (paths == null) return Collections.emptyList();
    return contenidoMultimediaService.mapeosMultimedia(paths);
  }

  @Override
  public List<HechoOutputDTO> buscarTodos() {
    return this.hechoRepository
        .findAll()
        .stream()
        .map(this::hechoOutputDTO)
        .toList();

  }

  @Override
  public void actualizarHecho(Hecho hecho, String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDateTime fechaAcontecimiento) {
    hecho.setTitulo(titulo);
    hecho.setDescripcion(descripcion);
    hecho.setCategoria(categoria);
    hecho.setUbicacion(ubicacion);
    hecho.setFechaAcontecimiento(fechaAcontecimiento);
    hecho.setFechaActualizacion(LocalDate.now());
  }

  private void actualizarMultimedia(
      Hecho hecho,
      MultipartFile[] multimedia,
      boolean replaceMedia,
      List<String> deleteExisting
  ) {

    List<ContenidoMultimedia> actuales =
        hecho.getContenidosMultimedia() != null
            ? new ArrayList<>(hecho.getContenidosMultimedia())
            : new ArrayList<>();

    if (replaceMedia) {
      actuales.forEach(c -> contenidoMultimediaRepository.delete(c));
      actuales.clear();
    }

    if (!replaceMedia && deleteExisting != null && !deleteExisting.isEmpty()) {

      Set<String> pathsAEliminar = new HashSet<>(deleteExisting);

      actuales.removeIf(c -> {
        boolean borrar = pathsAEliminar.contains(c.getPath());
        if (borrar) contenidoMultimediaRepository.delete(c);
        return borrar;
      });
    }

    if (multimedia != null && multimedia.length > 0) {

      List<String> nuevasUrls = new ArrayList<>();

      for (MultipartFile file : multimedia) {
        if (file != null && !file.isEmpty()) {
          String path = minioService.upload(file);  // ⭐ devuelve URL/Path final
          nuevasUrls.add(path);
        }
      }

      List<ContenidoMultimedia> nuevos = mapearMultimedia(nuevasUrls);

      actuales.addAll(nuevos);
    }

    hecho.setContenidosMultimedia(actuales);
  }



}
