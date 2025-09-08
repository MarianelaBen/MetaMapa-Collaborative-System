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
import java.util.Optional;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController implements IEstadisticaController {

  private final IEstadisticaService estadisticasService;
  private final IEstadisticaRepository estadisticaRepository;

  @Autowired
  public EstadisticaController(IEstadisticaService estadisticasService,
                               IEstadisticaRepository estadisticaRepository) {
    this.estadisticasService = estadisticasService;
    this.estadisticaRepository = estadisticaRepository;
  }

  // Todas las estadisticas
  @GetMapping
  public ResponseEntity<List<Estadistica>> todasLasEstadisticas() {
    return ResponseEntity.ok(
        estadisticaRepository.findAll()
    );
  }

  // Todas las estadisticas de un tipo
  @GetMapping("/{tipo}")
  public ResponseEntity<List<Estadistica>> estadisticasPorTipo(
      @PathVariable String tipo) {
    return ResponseEntity.ok(
        estadisticaRepository.findByTipo(tipo)
    );
  }

  // Última versión de un tipo
  @GetMapping("/{tipo}/ultima")
  public ResponseEntity<Estadistica> ultimaPorTipo(@PathVariable String tipo) {
    return ResponseEntity.of(
        Optional.ofNullable(estadisticaRepository.findFirstByTipoOrderByFechaCalculoDesc(tipo))
    );
  }

  // Estadísticas de una versión concreta
  @GetMapping("/version/{fecha}")
  public ResponseEntity<List<Estadistica>> porVersion(@PathVariable String fecha) {
    LocalDateTime fecha_calculo = LocalDateTime.parse(fecha);
    return ResponseEntity.ok(
        estadisticaRepository.findByFechaCalculo(fecha_calculo)
    );
  }

  @GetMapping(value = "/export", produces = "text/csv")
  public void exportarCSV(HttpServletResponse response) throws IOException {

    response.setHeader("Content-Disposition", "attachment; filename=estadisticas.csv");
    List<Estadistica> stats = estadisticaRepository.findAll();

    try (PrintWriter writer = response.getWriter()) {
      writer.println("id,tipo,clave,valor,fecha");

      for (Estadistica e : stats) {
        writer.printf("%d,%s,%s,%d,%s%n",
            e.getId(), e.getTipo(), e.getClave(), e.getValor(), e.getFechaCalculo());
      }
    }
  }

}

