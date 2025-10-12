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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
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


    @Override
    @Transactional
    public void eliminarColeccion(String handle) {
        Coleccion coleccion = coleccionRepo.findByHandle(handle)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se puede eliminar. Coleccion no encontrada con handle: " + handle));

        // forzamos inicialización de la colección si está LAZY (opcional pero seguro)
        coleccion.getHechos().size();

        coleccionRepo.delete(coleccion);
    }

    @Override
    public void eliminarHecho(Long id){
        Hecho hecho = hechoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se puede eliminar. Hecho no encontrado con id: " + id));
        hecho.setFueEliminado(true);
        hechoRepo.save(hecho);
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
    hechoOutputDTO.setId(hecho.getId());
    hechoOutputDTO.setTitulo(hecho.getTitulo());
    hechoOutputDTO.setDescripcion(hecho.getDescripcion());
    hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
    hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
    hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
    hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    hechoOutputDTO.setCantVistas(hecho.getCantVistas());
    if(hecho.getEtiquetas() != null){
      hechoOutputDTO.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
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
    Long hechoId = s.getHecho().getId();
        Hecho h = hechoRepo.findById(hechoId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el hecho " + hechoId));
        h.setFueEliminado(true);
    s.setEstado(EstadoSolicitud.ACEPTADA);
    s.setFechaAtencion(LocalDateTime.now());
    hechoRepo.save(h);
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  //Denegar una solicitud de eliminación de un hecho.
  @Override
  public SolicitudOutputDTO denegarSolicitud(Long id) {
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    s.setEstado(EstadoSolicitud.RECHAZADA);
      s.setFechaAtencion(LocalDateTime.now());
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  @Override
  public SolicitudOutputDTO getSolicitud(Long id){
    SolicitudDeEliminacion s = solicitudRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró la solicitud " + id));
    return SolicitudOutputDTO.fromEntity(solicitudRepo.save(s));
  }

  public InformeDeResultados procesarCsv(MultipartFile file) throws Exception {
    long tiempo0 = System.currentTimeMillis();

    String nombreOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivoNuevo.csv";
    Path destino = guardarArchivo(file);

    long total = 0;
    long guardados = 0;

    try (BufferedReader br = Files.newBufferedReader(destino, StandardCharsets.UTF_8);
         CSVReader reader = new CSVReaderBuilder(br).build()) {

      String[] header = reader.readNext();
      validarHeader(header);

      String[] fila;
      while ((fila = reader.readNext()) != null) {
        total++;
        Hecho h = leerArchivo(fila);
        hechoRepository.save(h);
        guardados++;
      }
    } catch (IOException | CsvValidationException e) {
      throw new RuntimeException("No se pudo leer el CSV: " + e.getMessage(), e);
    }

    long tiempo = System.currentTimeMillis() - tiempo0;

    return InformeDeResultados.builder()
        .nombreOriginal(nombreOriginal)
        .guardadoComo(destino.toString().replace('\\','/'))
        .hechosTotales(total)
        .guardadosTotales(guardados)
        .tiempoTardado(tiempo)
        .build();
  }

  private Path guardarArchivo(MultipartFile file) throws IOException {
    Path dir = Paths.get(importsDir);
    Files.createDirectories(dir);

    String nombre = UUID.randomUUID() + ".csv";
    Path destino = dir.resolve(nombre);

    try (InputStream in = file.getInputStream()) {
      Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
    }
    return destino;
  }

  private Hecho leerArchivo(String[] fila) {
    if (fila.length < 6) {
      throw new IllegalArgumentException("Fila con columnas insuficientes");
    }

    String titulo = trimOrNull(fila[0]);
    String descripcion = trimOrNull(fila[1]);
    String categoriaNombre = trimOrNull(fila[2]);
    String latStr = trimOrNull(fila[3]);
    String lonStr = trimOrNull(fila[4]);
    String fechaStr = trimOrNull(fila[5]);

    if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Título requerido");
    if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("Descripción requerida");
    if (categoriaNombre == null || categoriaNombre.isBlank()) throw new IllegalArgumentException("Categoría requerida");
    if (latStr == null || lonStr == null) throw new IllegalArgumentException("Coordenadas requeridas");
    if (fechaStr == null) throw new IllegalArgumentException("Fecha del hecho requerida");

    double latitud = Double.parseDouble(latStr);
    double longitud = Double.parseDouble(lonStr);

    LocalDate fecha = LocalDate.parse(fechaStr, FECHA_CSV);
    LocalDateTime fechaAcontecimiento = fecha.atStartOfDay();

    Categoria categoria = categoriaRepository //TODO AGREGAR EL NORMALIZADOR CUANDO HAGAMOS MERGE
        .findByNombreIgnoreCase(categoriaNombre)
        .orElseGet(() -> categoriaRepository.save(new Categoria(categoriaNombre)));

    Ubicacion ubicacion = new Ubicacion(latitud, longitud);

    Hecho h = new Hecho(
        titulo,
        descripcion,
        categoria,
        ubicacion,
        fechaAcontecimiento,
        LocalDate.now(),
        Origen.PROVENIENTE_DE_DATASET,
        null
    );
    return h;
  }

  private void validarHeader(String[] header) {
    if (header == null) throw new IllegalArgumentException("CSV vacío (sin encabezado)");

    List<String> esperado = List.of(
        "Título","Descripción","Categoría","Latitud","Longitud","Fecha del hecho"
    );
    List<String> recibido = Arrays.stream(header)
        .map(h -> h == null ? "" : h.trim())
        .toList();

    if (recibido.size() < esperado.size()) {
      throw new IllegalArgumentException("Header insuficiente. Se esperaban 6 columnas.");
    }
    for (int i = 0; i < esperado.size(); i++) {
      if (!esperado.get(i).equalsIgnoreCase(recibido.get(i))) {
        throw new IllegalArgumentException(
            "Header inválido en columna " + (i + 1) +
                ". Esperado: '" + esperado.get(i) + "', recibido: '" + recibido.get(i) + "'.");
      }
    }
  }

  private static String trimOrNull(String s) {
    return s == null ? null : s.replace('\u00A0',' ').trim();
  }
}
