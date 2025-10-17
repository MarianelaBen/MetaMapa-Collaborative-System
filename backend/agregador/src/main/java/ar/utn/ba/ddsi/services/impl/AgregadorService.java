package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ContribuyenteDTO;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Override
    public List<SolicitudOutputDTO> getSolicitudes() {
        return solicitudesRepo.findAll().stream()
                .map(SolicitudOutputDTO::fromEntity) //Convertimos cada entidad a DTO
                .collect(Collectors.toList());
    }

  @PersistenceContext
  private EntityManager em; //TODO PARA PRUEBAS PORQUE NO HAY CONTR REPO TDV

  @Override
  @Transactional //TODO PARA PRUEBAS PORQUE NO HAY CONTR REPO TDV
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
    if (fuentes == null || fuentes.isEmpty()) {
      throw new IllegalArgumentException("No se especificaron fuentes.");
    }
    List<Hecho> hechos = fuentes.stream()
        .flatMap( f-> obtenerTodosLosHechosDeFuente(f)
            .stream())
            .map(h -> {
              System.out.println(h);
              try { //para que si falla la normalizacion de un hecho no falle toda la normalizacion
                normalizadorService.normalizar(h);
                return h;
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    if (hechos.isEmpty()) {
      throw new NoSuchElementException("No se encontraron hechos para las fuentes indicadas.");
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

    public List<HechoOutputDTO> top3Hechos() {
        List<Hecho> hechos = this.hechoRepository.findTop3ByFueEliminadoFalseOrderByCantVistasDesc();
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
    hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
    hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutputDTO.setFueEliminado(hecho.getFueEliminado());

    if (hecho.getUbicacion() != null) {
      hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
      hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
      hechoOutputDTO.setProvincia(hecho.getUbicacion().getProvincia());
    } else {
      hechoOutputDTO.setLatitud(null);
      hechoOutputDTO.setLongitud(null);
      hechoOutputDTO.setProvincia(null);
    }

    if(hecho.getEtiquetas() != null){
      hechoOutputDTO.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
    }
    if(hecho.getPathMultimedia() != null){
      hechoOutputDTO.setIdContenidoMultimedia(new ArrayList<>(hecho.getPathMultimedia()));
    }
    if(hecho.getContribuyente() != null){
      ContribuyenteDTO contrDto = new ContribuyenteDTO();
      contrDto.setId(hecho.getContribuyente().getId());
      contrDto.setNombre(hecho.getContribuyente().getNombre());
      contrDto.setApellido(hecho.getContribuyente().getApellido());
      contrDto.setFechaDeNacimiento(hecho.getContribuyente().getFechaDeNacimiento());
      hechoOutputDTO.setContribuyente(contrDto);
    }
    if(hecho.getFuenteExterna() != null){
      hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    }

    // Cálculo edición: 7 días desde fechaCarga
//    LocalDate fc = hecho.getFechaCarga();
//    if (fc != null) {
//      long dias = ChronoUnit.DAYS.between(fc, LocalDate.now());
//      boolean editable = dias < 7;
//      hechoOutputDTO.setEditable(editable);
//      hechoOutputDTO.setDiasRestantesEdicion(editable ? (int) (7 - dias) : 0);
//    } else {
//      hechoOutputDTO.setEditable(false);
//      hechoOutputDTO.setDiasRestantesEdicion(0);
//    }

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
}