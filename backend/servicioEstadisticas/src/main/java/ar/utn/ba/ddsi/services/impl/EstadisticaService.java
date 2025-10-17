package ar.utn.ba.ddsi.services.impl;



import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
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

  private String agregadorController = "http://localhost:8083/api/public";
  private String adminController     = "http://localhost:8083/api/admin";

  @Autowired
  public EstadisticaService(IEstadisticaRepository estadisticaRepository,
                            WebClient.Builder webClientBuilder) {
    this.estadisticaRepository = estadisticaRepository;
    this.webClient = webClientBuilder.build();
  }

  @Override
  public void recalcularEstadisticas() {
    List<Hecho> todosLosHechos = this.obtenerTodosLosHechos();
    List<Coleccion> colecciones = this.obtenerColecciones();
    List<Categoria> categorias = this.obtenerCategorias();
    LocalDateTime ahora = LocalDateTime.now();

    //Provincia con mas hechos por coleccion
    colecciones.forEach(col -> {
      List<Hecho> hechosColeccion = obtenerHechosDeColeccion(col.getHandle())
          .stream()
          .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
          .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
          .toList();

      if (hechosColeccion.isEmpty()) return;

      Map<String, Long> conteo = hechosColeccion.stream()
          .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()));

      Map.Entry<String, Long> top = conteo.entrySet().stream()
          .max(Map.Entry.comparingByValue())
          .orElse(null);

      if (top == null) return;

      HechosPorProvinciaEnColeccion sub = new HechosPorProvinciaEnColeccion();
      sub.setColeccionHandle(col.getHandle());
      sub.setCantidadPorProvincia(conteo);
      sub.setProvinciaGanadora(top.getKey());
      sub.setCantidadGanadora(top.getValue());

      Estadistica snap = new Estadistica();
      snap.setFechaDeCalculo(ahora);
      snap.setHechosPorProvinciaEnColeccion(sub);

      estadisticaRepository.save(snap);
    });

    // 2) Categoria con mayor cantidad de hechos (global)
    {
      List<Hecho> activos = todosLosHechos.stream()
          .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
          .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
          .toList();

      if (!activos.isEmpty()) {
        Map<String, Long> conteo = activos.stream()
            .collect(Collectors.groupingBy(h -> h.getCategoria().getNombre(), Collectors.counting()));

        Map.Entry<String, Long> top = conteo.entrySet().stream()
            .max(Map.Entry.comparingByValue()).orElse(null);

        if (top != null) {
          CategoriaTopGlobal sub = new CategoriaTopGlobal();
          sub.setCantidadPorCategoria(conteo);
          sub.setCategoriaGanadora(top.getKey());
          sub.setCantidadGanadora(top.getValue());

          Estadistica snap = new Estadistica();
          snap.setFechaDeCalculo(ahora);
          snap.setCategoriaTopGlobal(sub);

          estadisticaRepository.save(snap);
        }
      }
    }

    //Provincia top por categoria
    categorias.forEach(cat -> {
      String nombreCat = cat.getNombre();
      if (nombreCat == null) return;

      List<Hecho> hechosCat = todosLosHechos.stream()
          .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
          .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
          .filter(h -> h.getCategoria().getNombre().equalsIgnoreCase(nombreCat))
          .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
          .toList();

      if (hechosCat.isEmpty()) return;

      Map<String, Long> conteo = hechosCat.stream()
          .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()));

      Map.Entry<String, Long> top = conteo.entrySet().stream()
          .max(Map.Entry.comparingByValue()).orElse(null);

      if (top == null) return;

      ProvinciaTopPorCategoria sub = new ProvinciaTopPorCategoria();
      sub.setCategoria(nombreCat);
      sub.setCantidadPorProvincia(conteo);
      sub.setProvinciaGanadora(top.getKey());
      sub.setCantidadGanadora(top.getValue());

      Estadistica snap = new Estadistica();
      snap.setFechaDeCalculo(ahora);
      snap.setProvinciaTopPorCategoria(sub);

      estadisticaRepository.save(snap);
    });

    //Hor pico por categoria
    categorias.forEach(cat -> {
      String nombreCat = cat.getNombre();
      if (nombreCat == null) return;

      List<Hecho> hechosCat = todosLosHechos.stream()
          .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
          .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
          .filter(h -> h.getCategoria().getNombre().equalsIgnoreCase(nombreCat))
          .filter(h -> h.getFechaYHoraAcontecimiento() != null) // mantenido tal cual tu método
          .toList();

      if (hechosCat.isEmpty()) return;

      Map<Integer, Long> conteo = hechosCat.stream()
          .collect(Collectors.groupingBy(
              h -> h.getFechaYHoraAcontecimiento().getHour(), // 0..23
              Collectors.counting()
          ));

      Map.Entry<Integer, Long> top = conteo.entrySet().stream()
          .max(Map.Entry.comparingByValue()).orElse(null);

      if (top == null) return;

      HorarioPicoPorCategoria sub = new HorarioPicoPorCategoria();
      sub.setCategoria(nombreCat);
      sub.setCantidadPorHora(conteo);
      sub.setHoraGanadora(top.getKey());
      sub.setCantidadGanadora(top.getValue());

      Estadistica snap = new Estadistica();
      snap.setFechaDeCalculo(ahora);
      snap.setHorarioPicoPorCategoria(sub);

      estadisticaRepository.save(snap);
    });

    //Solicitudes de eliminacin que son spam
    {
      long total = this.obtenerSolicitudes().size();
      long spam  = this.obtenerSolicitudes().stream()
          .filter(SolicitudDeEliminacion::getEsSpam)
          .count();

      SolicitudesEliminacionSpam sub = new SolicitudesEliminacionSpam();
      sub.setTotal(total);
      sub.setSpam(spam);

      Estadistica snap = new Estadistica();
      snap.setFechaDeCalculo(ahora);
      snap.setSolicitudesEliminacionSpam(sub);

      estadisticaRepository.save(snap);
    }
  }

  //metodos para llamadas
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