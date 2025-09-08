/* package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.services.IEstadisticasService;
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

  private final IEstadisticasService estadisticasService;
  private final IEstadisticasService estadisticaRepository;

  @Autowired
  public EstadisticaController(IEstadisticasService estadisticasService,
                               IEstadisticaRepository estadisticaRepository) {
    this.estadisticasService = estadisticasService;
    this.estadisticaRepository = estadisticaRepository;
  }

  @GetMapping
  public ResponseEntity<List<Estadistica>> todasLasEstadisticas() {
    return ResponseEntity.ok(
        estadisticaRepository.findAll()
    );
  }

  @GetMapping("/{tipo}")
  public ResponseEntity<List<Estadistica>> estadisticasPorTipo(
      @PathVariable String tipo) {
    return ResponseEntity.ok(
        estadisticaRepository.findByTipo(tipo)
    );
  }
}

*/