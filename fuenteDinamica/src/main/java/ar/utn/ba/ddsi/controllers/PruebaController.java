package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.impl.HechoRepository;
import ar.utn.ba.ddsi.services.IHechoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "*")
public class PruebaController {
  @Autowired
  private IHechoService hechoService;
  @Autowired
  private HechoRepository hechoRepository;

  @PostMapping
  public ResponseEntity<?> crearHecho(@RequestBody HechoInputDTO hechoInputDTO) {
    try {
      HechoOutputDTO hechoCreado = hechoService.crear(hechoInputDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(hechoCreado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al crear hecho", "mensaje", e.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> eliminarHecho(@PathVariable Long id) {
    try {
      hechoService.eliminar(id);
      return ResponseEntity.ok(Map.of("mensaje", "Hecho eliminado correctamente"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error", "Error al eliminar hecho", "mensaje", e.getMessage()));
    }
  }

  @GetMapping("/{idHecho}/permisos-edicion/{idEditor}")
  public ResponseEntity<?> verificarPermisosEdicion(
      @PathVariable Long idHecho,
      @PathVariable Long idEditor) {
    try {
      HechoOutputDTO hecho = hechoService.permisoDeEdicion(idEditor, idHecho);
      return ResponseEntity.ok(Map.of(
          "puedeEditar", true,
          "hecho", hecho
      ));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(Map.of(
              "puedeEditar", false,
              "error", "Sin permisos de edición",
              "mensaje", e.getMessage()
          ));
    }
  }

  @PutMapping("/{idHecho}/editar/{idEditor}")
  public ResponseEntity<?> editarHecho(
      @PathVariable Long idHecho,
      @PathVariable Long idEditor,
      @RequestBody HechoInputDTO hechoInputDTO) {
    try {
      HechoOutputDTO hechoEditado = hechoService.edicion(idEditor, hechoInputDTO, idHecho);
      return ResponseEntity.ok(hechoEditado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al editar hecho", "mensaje", e.getMessage()));
    }
  }

  @PostMapping("/{idHecho}/rechazar-creacion")
  public ResponseEntity<?> rechazarCreacion(@PathVariable Long idHecho) {
    try {
      Hecho hecho = hechoRepository.findById(idHecho);
      hechoService.creacionRechazada(hecho);
      return ResponseEntity.ok(Map.of("mensaje", "Creación rechazada correctamente"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al rechazar creación", "mensaje", e.getMessage()));
    }
  }

  @PostMapping("/{idHecho}/rechazar-edicion")
  public ResponseEntity<?> rechazarEdicion(@PathVariable Long idHecho) {
    try {
      Hecho hecho = hechoRepository.findById(idHecho);
      hechoService.edicionRechazada(hecho);
      return ResponseEntity.ok(Map.of("mensaje", "Edición rechazada correctamente"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al rechazar edición", "mensaje", e.getMessage()));
    }
  }

  @GetMapping
  public List<HechoOutputDTO> buscarTodas() {
    return this.hechoService.buscarTodos();
  }
}
