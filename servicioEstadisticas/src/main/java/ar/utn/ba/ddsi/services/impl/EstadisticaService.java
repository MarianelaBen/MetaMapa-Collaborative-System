package ar.utn.ba.ddsi.services.impl;


import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.models.repositories.IPreguntaRepository;
import ar.utn.ba.ddsi.services.IEstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticaService implements IEstadisticaService {

  private final WebClient webClient;
  private final IEstadisticaRepository estadisticaRepository;
  private final IPreguntaRepository preguntaRepository;

  private String agregadorController = "http://localhost:8083/api/public";
  private String adminController = "http://localhost:8083/api/admin";

  private String provinciaMasHechosColeccion = "De una coleccion, en que provincia se agrupan la mayor cantidad de hechos reportados?";
  private String categoriaMasHechos = "Cual es la categoria con mayor cantidad de hechos reportados?";
  private String provinciaMasHechoCategoria = "En que provincia se presenta la mayor cantidad de hechos de una cierta categoria?";
  private String horaMasHechosCategoria = "A que hora del dia ocurren la mayor cantidad de hechos de una cierta categoria?";
  private String solicitudesSonSpam = "Cuantas solicitudes de eliminacion son spam?";

  @Autowired
  public EstadisticaService(IEstadisticaRepository estadisticaRepository,
                            WebClient.Builder webClientBuilder,
                            IPreguntaRepository preguntaRepository) {
    this.estadisticaRepository = estadisticaRepository;
    this.webClient = webClientBuilder.build();
    this.preguntaRepository = preguntaRepository;
  }

  @Override
  public void recalcularEstadisticas() {
    //estos metodos llaman al agregador (api-Rest)
    List<Hecho> todosLosHechos = this.obtenerTodosLosHechos();
    List<Coleccion> colecciones = this.obtenerColecciones();
    List<Categoria> categorias = this.obtenerCategorias();

    LocalDateTime ahora = LocalDateTime.now();


    Pregunta provinciaMasHechos = pregunta(provinciaMasHechosColeccion);//busca en BD la entidad Pregunta cuyo texto coincide con eso
    Map<Coleccion, ProvinciaOutputDTO> provinciaPorColeccion = colecciones.stream()
            .map(c -> Map.entry(c, provinciaConMasHechosEnColeccion(c.getHandle()))) //para cada coleccion arma un entry con la coleccion y la provincia ganaroda dentro de esa coleccion
            .filter(e -> e.getValue() != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); //pasa a Coleccion,ProvinviaOutputDTO

    //para cada coleccion con su resultado guarda una estadistica
    provinciaPorColeccion.forEach((coleccion, provinciaDTO) -> {
      saveEst(provinciaMasHechos,
          coleccion.getHandle(),
          null,
          provinciaDTO.getProvincia(),
          null,
          provinciaDTO.getCantidad(),
          ahora);
    });


    Pregunta catMasHechos = pregunta(categoriaMasHechos);
    CategoriaOutputDTO catConMasHechos = categoriaConMasHechos(todosLosHechos); //calcula que categoria aparece mas entre todos los hechos y cuantos tiene
    if (catConMasHechos != null) {
      Long categoriaId = resolverCategoriaIdPorNombre(categorias, catConMasHechos.getNombre());
      saveEst(catMasHechos,
          null, categoriaId, null, null,
          catConMasHechos.getCantidad(),
          ahora);
    }


    Pregunta provPorCategoria = pregunta(provinciaMasHechoCategoria);
    Pregunta horaPorCategoria = pregunta(horaMasHechosCategoria);

    categorias.forEach(cat -> {
      ProvinciaOutputDTO provincia = provinciaConMasHechosParaCategoria(cat.getId(), todosLosHechos); //para cada categoria calcula en que provincia hubo mas hechos
      if (provincia != null) {
        saveEst(provPorCategoria, //si hay resultado guarda estadistica con categoria, provinciam cantidad
            null,
            cat.getId(),
            provincia.getProvincia(),
            null,
            provincia.getCantidad(),
            ahora);
      }

      HoraOutputDTO hora = horaConMasHechosParaCategoria(cat.getId(), todosLosHechos); //calcula dentro de esa categoria la hora con mas hechos
      if (hora != null && hora.getFechaYHoraAcontecimiento() != null) {
        saveEst(horaPorCategoria, //se guarda la estadistica con categoria y hora del dia (0 a 23) y la cantidad
            null,
            cat.getId(),
            null,
            hora.getFechaYHoraAcontecimiento().getHour(),
            hora.getCantidad(),
            ahora);
      }
    });


    Pregunta spam = pregunta(solicitudesSonSpam);
    long cantidadSpam = contarSolicitudesEliminacionSpam();
    saveEst(spam, null, null, null, null, cantidadSpam, ahora);
  }

  //metodos aux

  //busca pregunta por nombre
  private Pregunta pregunta(String pregunta) {
    return preguntaRepository.findByPregunta(pregunta)
        .orElseThrow(() -> new IllegalStateException("No existe la pregunta en BD: " + pregunta));
  }

  //crea una Estadistica con los datos recibidos y la guarda
  private void saveEst(Pregunta pregunta,
                       String coleccionHandle,
                       Long categoriaId,
                       String provincia,
                       Integer horaDelDia,
                       long valor,
                       LocalDateTime fechaDeCalculo) {
    Estadistica est = new Estadistica(
        pregunta,
        coleccionHandle,
        categoriaId,
        provincia,
        horaDelDia,
        valor,
        fechaDeCalculo
    );
    estadisticaRepository.save(est);
  }

  //dada una lista de categ y un nombre busca su id, esto lo hacemos para guardar la Estadistica referenciando a la categoria por su id no por su nombre
  private Long resolverCategoriaIdPorNombre(List<Categoria> existentes, String nombre) {
    if (nombre == null) return null;
    return existentes.stream()
        .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre))
        .map(Categoria::getId)
        .findFirst()
        .orElse(null);
  }


  //METODOS QUE TENIAMOS ANTES
  @Override
  public ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle) {
    List<Hecho> hechos = obtenerHechosDeColeccion(coleccionHandle)
        .stream()
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
        .toList();

    return hechos.stream()
        .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new ProvinciaOutputDTO(e.getKey(), Math.toIntExact(e.getValue())))
        .orElse(null);
        //devuelve un ProvinciaOutputDTO(prov, cant), y asi los demas
  }

  @Override
  public CategoriaOutputDTO categoriaConMasHechos(List<Hecho> hechos) {
    return hechos.stream()
        .filter(h -> h.getCategoria() != null)
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .collect(Collectors.groupingBy(Hecho::getCategoria, Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new CategoriaOutputDTO(e.getKey().getNombre(), e.getValue()))
        .orElse(null);
  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId, List<Hecho> hechos) {
    if (categoriaId == null) throw new IllegalArgumentException("Se debe especificar el id de la categoria.");

    String nombreCategoria = obtenerCategoria(categoriaId).getNombre();

    return hechos.stream()
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre().equalsIgnoreCase(nombreCategoria))
        .filter(h -> h.getUbicacion().getProvincia() != null)
        .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new ProvinciaOutputDTO(e.getKey(), Math.toIntExact(e.getValue())))
        .orElse(null);
  }

  @Override
  public HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId, List<Hecho> hechos) {
    if (categoriaId == null) throw new IllegalArgumentException("Se debe especificar el id de la categoria.");

    String nombreCategoria = this.obtenerCategoria(categoriaId).getNombre();

    return hechos.stream()
        .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
        .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre().equalsIgnoreCase(nombreCategoria))
        .filter(h -> h.getFechaYHoraAcontecimiento() != null)
        .collect(Collectors.groupingBy(
            h -> h.getFechaYHoraAcontecimiento().withMinute(0).withSecond(0).withNano(0),
            Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new HoraOutputDTO(e.getKey(), e.getValue()))
        .orElse(null);
  }

  @Override
  public long contarSolicitudesEliminacionSpam() {
    return this.obtenerSolicitudes()
        .stream()
        .filter(SolicitudDeEliminacion::getEsSpam)
        .count();
  }

  //METODOS QUE LLAMAN AL AGREGADOR
  public Categoria obtenerCategoria(Long categoriaId) {
    try {
      return webClient.get()
          .uri(agregadorController + "/categorias/" + categoriaId)
          .retrieve()
          .bodyToMono(Categoria.class)
          .block();
    } catch (WebClientResponseException e) {
      return null;
    }
  }

  public List<Coleccion> obtenerColecciones() {
    try {
      return webClient.get()
          .uri(adminController + "/colecciones")
          .retrieve()
          .bodyToFlux(Coleccion.class)
          .collectList()
          .block();
    } catch (WebClientResponseException e) {
      return List.of();
    }
  }

  public List<Categoria> obtenerCategorias() {
    try {
      return webClient.get()
          .uri(agregadorController + "/categorias")
          .retrieve()
          .bodyToFlux(Categoria.class)
          .collectList()
          .block();
    } catch (WebClientResponseException.NotFound e) {
      return List.of();
    }
  }

  public List<Hecho> obtenerHechosDeColeccion(String handle) {
    try {
      return webClient.get()
          .uri(agregadorController + "/colecciones/" + handle + "/hechos")
          .retrieve()
          .bodyToFlux(Hecho.class)
          .collectList()
          .block();
    } catch (WebClientResponseException.NotFound e) {
      return List.of();
    }
  }

  public List<Hecho> obtenerTodosLosHechos() {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder.path(agregadorController + "/hechos/fuentes").build())
          .retrieve()
          .bodyToFlux(Hecho.class)
          .collectList()
          .block();
    } catch (WebClientResponseException e) {
      return List.of();
    }
  }

  public List<SolicitudDeEliminacion> obtenerSolicitudes() {
    try {
      return webClient.get()
          .uri(agregadorController + "/solicitudes")
          .retrieve()
          .bodyToFlux(SolicitudDeEliminacion.class)
          .collectList()
          .block();
    } catch (WebClientResponseException.NotFound e) {
      return List.of();
    }
  }
}