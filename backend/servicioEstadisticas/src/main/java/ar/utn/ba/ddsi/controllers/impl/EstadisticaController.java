package ar.utn.ba.ddsi.controllers.impl;


import ar.utn.ba.ddsi.controllers.IEstadisticaController;
import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.services.IEstadisticaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController implements IEstadisticaController {

  private final IEstadisticaService estadisticasService;
  private final IEstadisticaRepository repo;

  @Autowired
  public EstadisticaController(IEstadisticaService estadisticasService,
                               IEstadisticaRepository repo) {
    this.estadisticasService = estadisticasService;
    this.repo = repo;
  }

  // General
  @GetMapping
  public ResponseEntity<List<Estadistica>> obtenerTodas() {
    return ResponseEntity.ok(repo.findAll());
  }

  // Hechos por provincia en colección
  @GetMapping("/hechos-por-provincia/coleccion/{handle}")
  public ResponseEntity<List<Estadistica>> hechosPorProvinciaEnColeccion(@PathVariable String handle) {
    return ResponseEntity.ok(
        repo.findAllByHechosPorProvinciaEnColeccion_ColeccionHandleOrderByFechaDeCalculoDesc(handle));
  }

  @GetMapping("/hechos-por-provincia/coleccion/{handle}/ultima")
  public ResponseEntity<Estadistica> hechosPorProvinciaEnColeccionUltima(@PathVariable String handle) {
    return repo.findTopByHechosPorProvinciaEnColeccion_ColeccionHandleOrderByFechaDeCalculoDesc(handle)
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Provincia top por categoría
  @GetMapping("/provincia-top-por-categoria/{categoriaId}")
  public ResponseEntity<List<Estadistica>> provinciaTopPorCategoria(@PathVariable Long categoriaId) {
    return ResponseEntity.ok(
        repo.findAllByProvinciaTopPorCategoria_CategoriaOrderByFechaDeCalculoDesc(categoriaId));
  }

  @GetMapping("/provincia-top-por-categoria/{categoriaId}/ultima")
  public ResponseEntity<Estadistica> provinciaTopPorCategoriaUltima(@PathVariable Long categoriaId) {
    return repo.findTopByProvinciaTopPorCategoria_CategoriaOrderByFechaDeCalculoDesc(categoriaId)
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Horario pico por categoría
  @GetMapping("/horario-pico-por-categoria/{categoriaId}")
  public ResponseEntity<List<Estadistica>> horarioPicoPorCategoria(@PathVariable Long categoriaId) {
    return ResponseEntity.ok(
        repo.findAllByHorarioPicoPorCategoria_CategoriaOrderByFechaDeCalculoDesc(categoriaId));
  }

  @GetMapping("/horario-pico-por-categoria/{categoriaId}/ultima")
  public ResponseEntity<Estadistica> horarioPicoPorCategoriaUltima(@PathVariable Long categoriaId) {
    return repo.findTopByHorarioPicoPorCategoria_CategoriaOrderByFechaDeCalculoDesc(categoriaId)
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Top categoría global (entre todos los hechos)
  @GetMapping("/top-categoria-global/ultima")
  public ResponseEntity<Estadistica> topCategoriaGlobalUltima() {
    return repo.findTopByTopCategoriaGlobal_CategoriaGanadoraIsNotNullOrderByFechaDeCalculoDesc()
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Solicitudes de eliminación (spam/total)
  @GetMapping("/solicitudes-eliminacion/ultima")
  public ResponseEntity<Estadistica> solicitudesEliminacionUltima() {
    return repo.findTopBySolicitudesEliminacionResumen_TotalIsNotNullOrderByFechaDeCalculoDesc()
        .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/export", produces = "text/csv")
  public void exportarCSV(HttpServletResponse resp) throws IOException {
    resp.setHeader("Content-Disposition", "attachment; filename=estadisticas.csv");

    List<Estadistica> todas = repo.findAll();

    try (PrintWriter w = resp.getWriter()) {
      // Encabezado "tall/narrow"
      w.println("id_estadistica,fecha_calculo,tipo,coleccion,categoria,hora,ganador,cantidad_ganadora,breakdown");

      for (Estadistica e : todas) {
        // 1) Hechos por provincia en una colección
        var hp = e.getHechosPorProvinciaEnColeccion();
        if (hp != null) {
          escribirFila(w, e,
              "HECHOS_POR_PROVINCIA_COLECCION",
              hp.getColeccionHandle(),
              null,
              null,
              hp.getProvinciaGanadora(),
              hp.getCantidadGanadora(),
              mapALinea(hp.getCantidadPorProvincia())
          );
        }

        // 2) Provincia top por categoría
        var pt = e.getProvinciaTopPorCategoria();
        if (pt != null) {
          escribirFila(w, e,
              "PROVINCIA_TOP_POR_CATEGORIA",
              null,
              nvl(pt.getCategoria()),
              null,
              pt.getProvinciaGanadora(),
              pt.getCantidadGanadora(),
              mapALinea(pt.getCantidadPorProvincia())
          );
        }

        // 3) Horario pico por categoría
        var hpico = e.getHorarioPicoPorCategoria();
        if (hpico != null) {
          Integer hora = hpico.getHoraGanadora();
          escribirFila(w, e,
              "HORARIO_PICO_POR_CATEGORIA",
              null,
              nvl(hpico.getCategoria()),
              hora,
              hora == null ? "" : hora.toString(),
              hpico.getCantidadGanadora(),
              mapHorasALinea(hpico.getCantidadPorHora())
          );
        }

        // 4) Top categoría global
        var top = e.getCategoriaTopGlobal();
        if (top != null) {
          escribirFila(w, e,
              "TOP_CATEGORIA_GLOBAL",
              null,
              null,
              null,
              nvl(top.getCategoriaGanadora()),
              top.getCantidadGanadora(),
              mapALinea(top.getCantidadPorCategoria())
          );
        }

        // 5)solicitudes de eliminación (dos filas: total y spam)
        var sol = e.getSolicitudesEliminacionSpam();
        if (sol != null) {
          escribirFila(w, e,
              "SOLICITUDES_ELIMINACION_TOTAL",
              null, null, null,
              "total",
              nvl(sol.getTotal()),
              null
          );
          escribirFila(w, e,
              "SOLICITUDES_ELIMINACION_SPAM",
              null, null, null,
              "spam",
              nvl(sol.getSpam()),
              null
          );
        }
      }
    }
  }

  /* helpers para armar el csv */

  private void escribirFila(PrintWriter w,
                            Estadistica e,
                            String tipo,
                            String coleccion,
                            String categoria,
                            Integer hora,
                            String ganador,
                            Long cantidadGanadora,
                            String breakdown) {
    w.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
        e.getId(),
        e.getFechaDeCalculo(),
        csv(tipo),
        csv(nvl(coleccion)),
        csv(nvl(categoria)),
        hora == null ? "" : hora.toString(),
        csv(nvl(ganador)),
        cantidadGanadora == null ? "" : cantidadGanadora.toString(),
        csv(nvl(breakdown))
    );
  }

  private String nvl(String s) { return s == null ? "" : s; }
  private Long   nvl(Long v)   { return v == null ? 0L : v; }

  // Escapa comillas y comas para CSV
  private String csv(String s) {
    if (s == null || s.isEmpty()) return "";
    boolean quote = s.contains(",") || s.contains("\"") || s.contains("\n");
    if (!quote) return s;
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }

  // "provincia=cant;provincia=cant"
  private String mapALinea(Map<String, Long> m) {
    if (m == null || m.isEmpty()) return "";
    return m.entrySet().stream()
        .map(e -> (e.getKey() == null ? "" : e.getKey()) + "=" + (e.getValue() == null ? 0 : e.getValue()))
        .collect(Collectors.joining(";"));
  }

  // "hora=cant;hora=cant"
  private String mapHorasALinea(Map<Integer, Long> m) {
    if (m == null || m.isEmpty()) return "";
    return m.entrySet().stream()
        .map(e -> (e.getKey() == null ? "" : e.getKey().toString()) + "=" + (e.getValue() == null ? 0 : e.getValue()))
        .collect(Collectors.joining(";"));
  }
}

