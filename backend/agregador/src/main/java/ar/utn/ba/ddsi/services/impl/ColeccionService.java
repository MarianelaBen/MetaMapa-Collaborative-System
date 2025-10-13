package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.Exceptions.ColeccionCreacionException;
import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
//import ar.utn.ba.ddsi.models.repositories.impl.FuenteRepository;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.modosDeNavegacion.IModoDeNavegacion;
import ar.utn.ba.ddsi.modosDeNavegacion.impl.ModoDeNavegacionFactory;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
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
  @Autowired
  private IHechoRepository hechoRepository;
  @Autowired
  private ICategoriaRepository categoriaRepository;

  private static final Path UPLOAD_DIR = Paths.get("uploads").normalize();

    @Override
  public Coleccion crearColeccion(Coleccion coleccion){
    if (!coleccion.getFuentes().isEmpty()){ this.filtrarHechos(coleccion); }

    return coleccionRepository.save(coleccion);
  }

  @Override
  public ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto) {
    try {
      Set<Fuente> fuentes = dto.getFuenteIds().stream()
          .map(fuenteId -> fuenteRepo.findById(fuenteId)
              .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + fuenteId)))
          .collect(Collectors.toSet());


      Coleccion c = new Coleccion(dto.getTitulo(), dto.getDescripcion(), fuentes);
      c.setHandle(dto.getHandle());
      c.setAlgoritmoDeConsenso(dto.getAlgoritmoDeConsenso());

      return ColeccionOutputDTO.fromEntity(this.crearColeccion(c));
    } catch (Exception e) {
      throw new ColeccionCreacionException("Error al crear la coleccion: " + e.getMessage());
    }
  }


  public Coleccion findById(String id) {
    return coleccionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Colección no encontrada con id: " + id));
  }

  public Coleccion filtrarHechos(Coleccion coleccion){
    coleccion.getHechos().clear();
    List<Hecho> hechosFiltrados = agregadorService.obtenerTodosLosHechos(coleccion.getFuentes())
        .stream()
        .filter(hecho -> !hecho.getFueEliminado())
        .collect(Collectors.toList());

    if( coleccion.getCriterios().isEmpty() ) { coleccion.agregarHechos(hechosFiltrados); }
    else { coleccion.agregarHechos(hechosFiltrados.stream()
        .filter(coleccion::cumpleLosCriterios)
        .collect(Collectors.toList())); }
    return coleccion;
  }

  @Override
  public void actualizarColecciones(){
    List<Coleccion> coleccionesExistentes = new ArrayList<>(coleccionRepository.findAll());
    for (Coleccion coleccion : coleccionesExistentes) {
      filtrarHechos(coleccion);
      coleccionRepository.save(coleccion);
    }
  }

  @Transactional
  public HechoOutputDTO actualizarHecho(Long id, HechoInputDTO input, MultipartFile[] multimedia, boolean replaceMedia, List<String> deleteExisting) {
    Hecho h = hechoRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado"));

    if (input.getTitulo() == null || input.getTitulo().isBlank()) throw new IllegalArgumentException("Título obligatorio");
    if (input.getDescripcion() == null || input.getDescripcion().isBlank()) throw new IllegalArgumentException("Descripción obligatoria");
    if (input.getCategoria() == null || input.getCategoria().isBlank()) throw new IllegalArgumentException("Categoría obligatoria");
    if (input.getFechaAcontecimiento() == null) throw new IllegalArgumentException("Fecha/hora del acontecimiento obligatoria");

    Categoria categoria = categoriaRepository.findByNombreIgnoreCase(input.getCategoria())
        .orElseGet(() -> {
          Categoria c = new Categoria();
          c.setNombre(input.getCategoria());
          return categoriaRepository.save(c);
        });

    // Asignar campos
    h.setTitulo(input.getTitulo());
    h.setDescripcion(input.getDescripcion());
    h.setCategoria(categoria);
    h.setFechaAcontecimiento(input.getFechaAcontecimiento());
    if (h.getFechaCarga() == null) h.setFechaCarga(LocalDate.now());
    h.setFuenteExterna(input.getFuenteExterna());

    // Ubicación
    if (h.getUbicacion() == null) h.setUbicacion(new Ubicacion());
    h.getUbicacion().setProvincia(input.getProvincia());
    h.getUbicacion().setLatitud(input.getLatitud());
    h.getUbicacion().setLongitud(input.getLongitud());

    // Multimedia
    List<String> pathsActuales = h.getPathMultimedia() == null ? new ArrayList<>() : new ArrayList<>(h.getPathMultimedia());
    if (replaceMedia) pathsActuales.clear();

    if (replaceMedia) {
      for (String p : pathsActuales) deletePhysical(p);
      pathsActuales.clear();
    }

    if (!replaceMedia && deleteExisting != null && !deleteExisting.isEmpty()) {
      Set<String> aBorrar = deleteExisting.stream()
          .filter(Objects::nonNull)
          .map(this::filenameOf)
          .collect(java.util.stream.Collectors.toSet());

      // quitamos del modelo
      pathsActuales.removeIf(stored -> aBorrar.contains(filenameOf(stored)));

      // OPCIONAL: borrar del disco por filename
      for (String s : aBorrar) deletePhysicalByFilename(s);
    }

    if (multimedia != null && multimedia.length > 0) {
      for (MultipartFile file : multimedia) {
        if (file != null && !file.isEmpty()) {
          try {
            String rutaWeb = guardarArchivoEnUploads(h.getId(), file);
            pathsActuales.add(rutaWeb);
          } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar archivo: " + file.getOriginalFilename(), e);
          }
        }
      }
    }
    h.setPathMultimedia(pathsActuales);

    Hecho saved = hechoRepository.save(h);
    return HechoOutputDTO.fromEntity(saved);
  }

  @Override
  public List<Hecho> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modoNavegacion) {
    Coleccion coleccion = coleccionRepository.findById(coleccionId)
        .orElseThrow(() -> new RuntimeException("No se encontró la coleccion con id: " + coleccionId));

    List<Hecho> hechos = coleccion.getHechos().stream()
        .filter(h -> !h.getFueEliminado())
        .collect(Collectors.toList());

    IModoDeNavegacion modo = modoDeNavegacionFactory.resolver(modoNavegacion);

    return modo.aplicarModo(hechos, coleccion.getAlgoritmoDeConsenso());
  }

    @Transactional
    public HechoOutputDTO subirHecho(HechoInputDTO input, MultipartFile[] multimedia) {
        // validaciones básicas
        if (input.getTitulo() == null || input.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título obligatorio");
        }
        if (input.getDescripcion() == null || input.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Descripción obligatoria");
        }
        if (input.getCategoria() == null || input.getCategoria().isBlank()) {
            throw new IllegalArgumentException("Categoría obligatoria");
        }
        if (input.getFechaAcontecimiento() == null) {
            throw new IllegalArgumentException("Fecha/hora del acontecimiento obligatoria");
        }

        // buscar o crear la categoria (según tu lógica)
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(input.getCategoria())
                .orElseGet(() -> {
                    Categoria c = new Categoria();
                    c.setNombre(input.getCategoria());
                    return categoriaRepository.save(c);
                });

        // crear entidad Hecho
        Hecho hecho = new Hecho();
        hecho.setTitulo(input.getTitulo());
        hecho.setDescripcion(input.getDescripcion());
        hecho.setCategoria(categoria);
        hecho.setFechaAcontecimiento(input.getFechaAcontecimiento());
        hecho.setFechaCarga(LocalDate.now());
        hecho.setFuenteExterna(input.getFuenteExterna());
        hecho.setFueEliminado(false);

        // Ubicacion: adaptá según tu clase Ubicacion (si tenés lat/long en el form, guardalos)
        Ubicacion ub = new Ubicacion();
        ub.setProvincia(input.getProvincia());
        ub.setLatitud(input.getLatitud());
        ub.setLongitud(input.getLongitud());
        hecho.setUbicacion(ub);

        // Save preliminary to get ID for filenames (opcional)
        Hecho saved = hechoRepository.save(hecho);

        // manejar archivos multimedia
        List<String> paths = new ArrayList<>();
        if (multimedia != null && multimedia.length > 0) {
            for (MultipartFile file : multimedia) {
                if (file != null && !file.isEmpty()) {
                    try {
                      String rutaWeb = guardarArchivoEnUploads(saved.getId(), file);
                      paths.add(rutaWeb);
                    } catch (IOException e) {
                        // si falla guardar un archivo, podés decidir rollback o ignorar;
                        // Aquí lanzo RuntimeException para forzar rollback
                        throw new RuntimeException("No se pudo guardar archivo: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        // setear paths y actualizar la entidad
        saved.setPathMultimedia(paths);
        saved = hechoRepository.save(saved);

        // convertir a OutputDTO (puedes usar tu helper)
        HechoOutputDTO dto = new HechoOutputDTO();
        dto.setId(saved.getId());
        dto.setTitulo(saved.getTitulo());
        dto.setDescripcion(saved.getDescripcion());
        dto.setCategoria(saved.getCategoria() != null ? saved.getCategoria().getNombre() : null);
        dto.setFechaAcontecimiento(saved.getFechaAcontecimiento());
        dto.setFechaCarga(saved.getFechaCarga());
        dto.setIdContenidoMultimedia(saved.getPathMultimedia());
        // etc: completar otros campos si querés

        return dto;
    }

  private static String safeFileName(String original) {
    String base = Paths.get(original).getFileName().toString(); // saca directorios tipo C:\...
    return base.replaceAll("[^a-zA-Z0-9._-]", "_");
  }

  private String guardarArchivoEnUploads(Long hechoId, MultipartFile file) throws IOException {
    Files.createDirectories(UPLOAD_DIR);
    String nombre = hechoId + "_" + System.currentTimeMillis() + "_" + safeFileName(file.getOriginalFilename());
    Path destino = UPLOAD_DIR.resolve(nombre);
    try (InputStream in = file.getInputStream()) {
      Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
    }
    return "/uploads/" + nombre;
  }

  private String filenameOf(String pathOrUrl) {
    if (pathOrUrl == null) return null;
    int i = pathOrUrl.lastIndexOf('/');
    return (i >= 0) ? pathOrUrl.substring(i + 1) : pathOrUrl;
  }

  private void deletePhysical(String pathOrUrl) {
    deletePhysicalByFilename(filenameOf(pathOrUrl));
  }

  private void deletePhysicalByFilename(String filename) {
    if (filename == null || filename.isBlank()) return;
    try {
      Path target = UPLOAD_DIR.resolve(filename).normalize();
      if (target.startsWith(UPLOAD_DIR)) {
        java.nio.file.Files.deleteIfExists(target);
      }
    } catch (IOException ignore) {
      // opcional: log.warn("No se pudo borrar {}", filename, ignore);
    }
  }
}

