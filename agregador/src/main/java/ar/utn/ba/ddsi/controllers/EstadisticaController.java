package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.repositories.EstadisticaRepository;
import ar.utn.ba.ddsi.services.impl.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

  private final EstadisticasService estadisticasService;
  private final EstadisticaRepository estadisticaRepository;

  @Autowired
  public EstadisticaController(EstadisticasService estadisticasService, EstadisticaRepository estadisticaRepository) {
    this.estadisticasService = estadisticasService;
    this.estadisticaRepository = estadisticaRepository;
  }

  // Endpoint para recalcular manual
  @PostMapping("/recalcular")
  public ResponseEntity<Void> recalcular() {
    estadisticasService.recalcularEstadisticas();
    return ResponseEntity.ok().build();
  }

  // Consultar última estadística de un tipo
  @GetMapping("/{tipo}/ultima")
  public ResponseEntity<Estadistica> ultimaEstadistica(@PathVariable String tipo) {
    return estadisticaRepository.findTopByTipoOrderByFechaAcontecimiento(tipo)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // Consultar historial de estadísticas por tipo
  @GetMapping("/{tipo}/historial")
  public List<Estadistica> historialPorTipo(@PathVariable String tipo) {
    return estadisticaRepository.findByTipoOrderByFechaAcontecimiento(tipo);
  }
}

