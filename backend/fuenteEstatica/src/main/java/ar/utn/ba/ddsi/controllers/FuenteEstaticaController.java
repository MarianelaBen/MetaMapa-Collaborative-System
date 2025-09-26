package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.RutaInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.RutaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Ruta;
import ar.utn.ba.ddsi.services.IFuenteEstaticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

  @PostMapping("/rutas")
  public ResponseEntity<?> agregarRuta(@RequestBody RutaInputDTO dto) {
    try {
      RutaOutputDTO rutaOutput = fuenteEstaticaService.crearRuta(dto.getNombre(), dto.getPath());
      return ResponseEntity.status(HttpStatus.CREATED).body(rutaOutput);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "No se pudo crear la ruta", "detalle", e.getMessage()));
    }
  }

  @PostMapping("/rutas/leer-todos")
  public ResponseEntity<?> leerTodasLasRutas() {
    try {
      fuenteEstaticaService.leerTodosLosArchivos();
      return ResponseEntity.ok(Map.of(
          "ok", true,
          "mensaje", "Se leyeron todos los archivos correctamente"
      ));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of(
              "error", "No se pudieron importar todas las rutas",
              "detalle", e.getMessage()
          ));
    }
  }
}
