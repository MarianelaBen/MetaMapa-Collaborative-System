package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {
  @Autowired
  IColeccionService coleccionService;
private final AdapterFuenteDinamica adapterFuenteDinamica;
private final AdapterFuenteEstatica adapterFuenteEstatica;
private final AdapterFuenteProxy adapterFuenteProxy;
private  final NormalizadorService normalizadorService;
private final IHechoRepository hechoRepository;
private final ICategoriaRepository categoriaRepository; //TODO BORRAR CUANDO SE ARREGLE NORMALIZADOR
    private final ISolicitudRepository solicitudesRepo;
    private final IColeccionRepository coleccionRepo;

public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica, AdapterFuenteEstatica adapterFuenteEstatica, AdapterFuenteProxy adapterFuenteProxy, NormalizadorService normalizadorService, IHechoRepository hechoRepository, ICategoriaRepository categoriaRepository, ISolicitudRepository solicitudesRepo, IColeccionRepository coleccionRepo) {
  this.adapterFuenteDinamica = adapterFuenteDinamica;
  this.adapterFuenteEstatica = adapterFuenteEstatica;
  this.adapterFuenteProxy = adapterFuenteProxy;
  this.normalizadorService = normalizadorService;
  this.hechoRepository = hechoRepository;
  this.categoriaRepository = categoriaRepository;
  this.solicitudesRepo = solicitudesRepo;
  this.coleccionRepo = coleccionRepo;
}

  @Value("${imports.dir:data/imports}") // configurable
  private String importsDir;

  private static final DateTimeFormatter FECHA_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @Override
    public List<SolicitudOutputDTO> getSolicitudes() {
        return solicitudesRepo.findAll().stream()
                .map(SolicitudOutputDTO::fromEntity) //Convertimos cada entidad a DTO
                .collect(Collectors.toList());
    }

  @Override
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
    if (fuentes == null || fuentes.isEmpty()) {
      throw new IllegalArgumentException("No se especificaron fuentes.");
    }
    List<Hecho> hechos = fuentes.stream()
        .flatMap( f-> obtenerTodosLosHechosDeFuente(f)
            .stream())
            //.map(h -> { //TODO ARREGLAR NORMALIZADOR
            //  System.out.println(h);
            //  try { //para que si falla la normalizacion de un hecho no falle toda la normalizacion
            //    normalizadorService.normalizar(h);
            //    return h;
            //  } catch (IllegalArgumentException e) {
            //    return null;
            //  }
            //})
        //.filter(Objects::nonNull)
        .collect(Collectors.toList());
    if (hechos.isEmpty()) {
      throw new NoSuchElementException("No se encontraron hechos para las fuentes indicadas.");
    }

    for (Hecho h : hechos) {//todo borrar con normlaizacion ya se arregla
      String nombre = (h.getCategoria() != null && h.getCategoria().getNombre() != null)
          ? h.getCategoria().getNombre()
          : "OTROS";
      h.setCategoria(ensureCategoria(nombre));
    }
    hechoRepository.saveAll(hechos); //TODO COMO EVITAR DUPLICADOS
    return hechos;
}

    @Override
    public List<HechoOutputDTO> obtenerHechos() {
        return this.hechoRepository.findAll().stream()
                .map(this::hechoOutputDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void sumarVistaColeccion(String handle){
        Coleccion c = coleccionRepo.findByHandle(handle)
                .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + handle));

        Integer actuales = c.getCantVistas();
        if (actuales == null) actuales = 0;
        c.setCantVistas(actuales + 1);

        coleccionRepo.save(c);
    }

    @Override
    public void sumarVistaHecho(Long id){
        Hecho h = hechoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + id));

        Integer actuales = h.getCantVistas();
        if (actuales == null) actuales = 0;
        h.setCantVistas(actuales + 1);

        hechoRepository.save(h);
    }

    public List<HechoOutputDTO> top4Hechos() {
        List<Hecho> hechos = this.hechoRepository.findTop4ByFueEliminadoFalseOrderByCantVistasDesc();
        return hechos.stream().map(this::hechoOutputDTO).collect(Collectors.toList());
    }

    public List<ColeccionOutputDTO> top4Colecciones() {
        List<Coleccion> cols = this.coleccionRepo.findTop4ByOrderByCantVistasDesc();
        return cols.stream().map(ColeccionOutputDTO::fromEntity).collect(Collectors.toList());
    }


  private Categoria ensureCategoria(String nombre) { //todo borrar con normlaizacion ya se arregla
    return categoriaRepository.findByNombreIgnoreCase(nombre)
        .orElseGet(() -> categoriaRepository.save(new Categoria(nombre)));
  }

  @Override
  public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente) {
    List<Hecho> hechos = new ArrayList<>();
    switch (fuente.getTipo()){
      case ESTATICA:
        hechos.addAll(adapterFuenteEstatica.obtenerHechos(fuente.getUrl()));
        break;
      case PROXY:

        hechos.addAll(adapterFuenteProxy.obtenerHechos(fuente.getUrl()));
        break;
      case DINAMICA:

        hechos.addAll(adapterFuenteDinamica.obtenerHechos(fuente.getUrl()));
        break;
    }

    return hechos;
  }

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO hechoOutputDTO = new HechoOutputDTO();
    hechoOutputDTO.setCantVistas(hecho.getCantVistas());
    hechoOutputDTO.setId(hecho.getId());
    hechoOutputDTO.setTitulo(hecho.getTitulo());
    hechoOutputDTO.setDescripcion(hecho.getDescripcion());
    hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
    hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
    hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
    hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFueEliminado(hecho.getFueEliminado());
    System.out.println(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    hechoOutputDTO.setProvincia(hecho.getUbicacion() != null ? hecho.getUbicacion().getProvincia() : null);
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


  //API PUBLICA

  //Consulta de hechos dentro de una colección.
  @Override
  public List<HechoOutputDTO> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modo){
    List<Hecho> hechos = coleccionService.obtenerHechosPorColeccion(coleccionId, modo);
    if (hechos == null) {
      throw new NoSuchElementException("Coleccion no encontrada: " + coleccionId);
    }
    return hechos
        .stream()
        .map(this::hechoOutputDTO)
        .toList();
  }


  //Navegación filtrada sobre una colección.
  @Override
  public List<HechoOutputDTO> obtenerHechosFiltrados(String coleccionId,String categoria, String fechaDesde, String fechaHasta){
    List<HechoOutputDTO> hechos = obtenerHechosPorColeccion(coleccionId, TipoDeModoNavegacion.IRRESTRICTA);
    if (categoria != null ) {
      hechos = hechos.stream()
          .filter(h -> h.getCategoria() != null && categoria.trim().equalsIgnoreCase(h.getCategoria().trim()))
          .collect(Collectors.toList());
    }

    // Filtro por fecha acontecimiento
    if (fechaDesde != null || fechaHasta != null) {
      hechos = hechos.stream()
          .filter(h -> {
            if (h.getFechaAcontecimiento() == null) return false;

            boolean afterDesde = true;
            boolean beforeHasta = true;

            if (fechaDesde != null) {
              LocalDateTime desde = LocalDateTime.parse(fechaDesde);
              afterDesde = !h.getFechaAcontecimiento().isBefore(desde);
            }
            if (fechaHasta != null) {
              LocalDateTime hasta = LocalDateTime.parse(fechaHasta);
              beforeHasta = !h.getFechaAcontecimiento().isAfter(hasta);
            }

            return afterDesde && beforeHasta;
          })
          .collect(Collectors.toList());
    }
    return hechos;

  }

  public InformeDeResultados procesarCsv(MultipartFile file) throws Exception {
    long tiempo0 = System.currentTimeMillis();

    String nombreOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivoNuevo.csv";
    Path destino = guardarArchivo(file); // ./data/imports/UUID.csv

    long total = 0;
    long guardados = 0;

    // leemos de disco (estable, no consume memoria de más)
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