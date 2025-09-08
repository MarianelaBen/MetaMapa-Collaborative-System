package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import ar.utn.ba.ddsi.services.IEstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EstadisticasService implements IEstadisticasService {

  private final IAgregadorService agregadorService;
  private final IColeccionRepository coleccionRepo;
  private final ISolicitudRepository solicitudRepo;
  private final ICategoriaRepository categoriaRepo;
  private final IDetectorDeSpam detectorDeSpam;
  private final IEstadisticaRepository estadisticaRepository;
  private final IFuenteRepository fuenteRepository;

  @Autowired
  public EstadisticasService(IAgregadorService agregadorService, IColeccionRepository coleccionRepository, ISolicitudRepository solicitudRepo, ICategoriaRepository categoriaRepo, IDetectorDeSpam detectorDeSpam, IEstadisticaRepository estadisticaRepository, IFuenteRepository fuenteRepository) {
    this.agregadorService = agregadorService;
    this.coleccionRepo = coleccionRepository;
    this.solicitudRepo = solicitudRepo;
    this.categoriaRepo = categoriaRepo;
    this.detectorDeSpam = detectorDeSpam;
    this.estadisticaRepository = estadisticaRepository;
    this.fuenteRepository = fuenteRepository;
  }

  @Override
  public void recalcularEstadisticas() {

    Set<Fuente> fuentes = new HashSet<>(fuenteRepository.findAll()); // hago new HashSet<> porque JPA sino define List en repo

    // Provincia con mas hechos
    ProvinciaOutputDTO provinciaMasHechos = provinciaConMasHechosEnColeccion("coleccion-principal");
    if (provinciaMasHechos != null) {
      Estadistica e = new Estadistica(
          "provincia_mas_hechos",
          provinciaMasHechos.getProvincia(),
          provinciaMasHechos.getCantidad(),
          LocalDateTime.now()
      );
      estadisticaRepository.save(e);
    }

    // Categoria con mas hechos
    CategoriaOutputDTO categoriaMasHechos = categoriaConMasHechos(fuentes);
    if (categoriaMasHechos != null) {
      Estadistica e = new Estadistica(
          "categoria_mas_hechos",
          categoriaMasHechos.getNombre(),
          categoriaMasHechos.getCantidad(),
          LocalDateTime.now()
      );
      estadisticaRepository.save(e);
    }

    // Para cada categoria, provincia y hora con mas hechos
    categoriaRepo.findAll().forEach(cat -> {
      ProvinciaOutputDTO provincia = provinciaConMasHechosParaCategoria(cat.getId(), fuentes);
      HoraOutputDTO hora = horaConMasHechosParaCategoria(cat.getId(), fuentes);

      if (provincia != null) {
        Estadistica e = new Estadistica(
            "provincia_mas_hechos_por_categoria",
            cat.getNombre() + " -> " + provincia.getProvincia(),
            provincia.getCantidad(),
            LocalDateTime.now()
        );
        estadisticaRepository.save(e);
      }

      if (hora != null) {
        Estadistica e = new Estadistica(
            "hora_mas_hechos_por_categoria",
            cat.getNombre() + " -> " + hora.getHoraAcontecimiento().toString(),
            hora.getCantidad(),
            LocalDateTime.now()
        );
        estadisticaRepository.save(e);
      }
    });

    // Solicitudes de eliminación spam
    long cantidadSpam = contarSolicitudesEliminacionSpam();
    Estadistica spamEst = new Estadistica(
        "solicitudes_eliminacion_spam",
        "total",
        cantidadSpam,
        LocalDateTime.now()
    );
    estadisticaRepository.save(spamEst);
  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle) {
    Coleccion coleccion = coleccionRepo.findById(coleccionHandle)
        .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + coleccionHandle));

    List<Hecho> hechos = coleccion.getHechos()
        .stream()
        .filter(h-> Boolean.FALSE.equals(h.getFueEliminado())) //si tira null, no pasa el filtro y NO tira nullPointerException
        .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
        //otra opcion si queremos que tire la excepcion si encuentra null: .filter(h-> !h.getFueEliminado())
        .toList();
    return hechos.stream()
        .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new ProvinciaOutputDTO(e.getKey(), e.getValue()) )
        .orElse(null);
  }

  @Override
  public CategoriaOutputDTO categoriaConMasHechos(Set<Fuente> fuentes) {

    List<HechoOutputDTO> hechos = agregadorService.obtenerTodosLosHechos(fuentes);

    return hechos.stream()
        .filter(h -> h.getCategoria() != null )
        .filter(h-> Boolean.FALSE.equals(h.getFueEliminado()))
        .collect(Collectors.groupingBy(h -> h.getCategoria(), Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new CategoriaOutputDTO(e.getKey(), e.getValue()))
        .orElse(null);
  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId, Set<Fuente> fuentes) {
    if (categoriaId == null) {
      throw new IllegalArgumentException("Se debe especificar el id de la categoria. ");
    }
    if(fuentes == null || fuentes.isEmpty()){
      throw new IllegalArgumentException("Se deben especificar la/las fuente/fuentes");
    }

    String nombreCategoria = categoriaRepo.findById(categoriaId)
        .map(Categoria::getNombre)
        .orElseThrow(() -> new IllegalArgumentException("No existe la categoria con id: " + categoriaId));

    List<HechoOutputDTO> hechos = agregadorService.obtenerTodosLosHechos(fuentes);

    return hechos.stream()
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .filter(h -> h.getCategoria() != null && h.getCategoria().equalsIgnoreCase(nombreCategoria))
        .filter(h -> h.getProvincia() != null )
        .collect(Collectors.groupingBy(h -> h.getProvincia(), Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new ProvinciaOutputDTO(e.getKey(), e.getValue())) // nombre + cantidad
        .orElse(null);
  }

  @Override
  public HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId, Set<Fuente> fuentes) {
    if(categoriaId == null){
      throw new IllegalArgumentException("Se debe especificar el id de la categoria. ");
    }
    if(fuentes == null || fuentes.isEmpty()){
      throw new IllegalArgumentException("Se deben especificar la/las fuente/fuentes");
    }

    String nombreCategoria = categoriaRepo.findById(categoriaId)
        .map(Categoria::getNombre)
        .orElseThrow(() -> new IllegalArgumentException("No la categoria con id: " + categoriaId));

    List<HechoOutputDTO> hechos = agregadorService.obtenerTodosLosHechos(fuentes);

    return hechos.stream()
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .filter(h -> h.getCategoria() != null && h.getCategoria().equalsIgnoreCase(nombreCategoria))
        .filter(h -> h.getHoraAcontecimiento() != null)
        .collect(Collectors.groupingBy(h -> h.getHoraAcontecimiento().withMinute(0).withSecond(0).withNano(0), Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new HoraOutputDTO(e.getKey(),e.getValue()))
        .orElse(null);
  }

  @Override
  public long contarSolicitudesEliminacionSpam() { //long en vez de Long por lo que devuelve el count()
    return solicitudRepo.findAll()
        .stream()
        .map(SolicitudDeEliminacion::getJustificacion)
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(s -> !s.isEmpty()) //para no pasar espacios
        .filter(detectorDeSpam::esSpam)
        .count();
  }

}
