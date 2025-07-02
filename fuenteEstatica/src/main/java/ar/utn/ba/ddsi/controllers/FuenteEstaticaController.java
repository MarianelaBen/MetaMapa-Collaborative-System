package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.services.IFuenteEstaticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "*")
public class FuenteEstaticaController {
  @Autowired
  private IFuenteEstaticaService fuenteEstaticaService;

  @GetMapping
  public ResponseEntity<?> getHechos() {
    try {
      return ResponseEntity.ok(this.fuenteEstaticaService.buscarTodos());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar los hechos", "detalle", e.getMessage()));
    }
  }
}
