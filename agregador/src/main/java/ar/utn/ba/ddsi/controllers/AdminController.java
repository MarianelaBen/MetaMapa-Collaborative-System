package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.FuenteOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.services.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final IAdminService servicio;

  public AdminController(IAdminService servicio) {
    this.servicio = servicio;
  }

  //Obtener la lista completa de colecciones
  @GetMapping("/colecciones")

  public ResponseEntity<?> getColecciones() {
    try{
      return ResponseEntity.ok(this.servicio.getColecciones());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar las colecciones","detalle", e.getMessage() ));
    }
  }

  //Crear una nueva colección.
  @PostMapping("/colecciones") //Recibe un DTO con los datos de la coleccion
  public ResponseEntity<?> crearColeccion(@RequestBody ColeccionInputDTO dto) {
    try{
      ColeccionOutputDTO coleccionCreada = servicio.crearColeccion(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(coleccionCreada);
    }catch(Exception e){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al crear colección", "mensaje", e.getMessage()));
    }

  }


  //Actualiza coleccion por id
  @PutMapping("/colecciones/{id}")
  public ResponseEntity<?> actualizarColeccion(@PathVariable String id, @RequestBody ColeccionInputDTO dto) {
    try{
      ColeccionOutputDTO coleccionEditada = servicio.actualizarColeccion(id, dto);
      return ResponseEntity.ok(coleccionEditada);
    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "Error al editar la colección", "mensaje", e.getMessage()));
    }
  }


  //Borra coleccion por su id
  @DeleteMapping("/colecciones/{id}")
  public ResponseEntity<?> eliminarColeccion(@PathVariable String id) {
    try{
      servicio.eliminarColeccion(id);
      return ResponseEntity.ok(Map.of("mesaje", "Coleccion borrada correctamente"));
    } catch(Exception e) {
      return ResponseEntity.status((HttpStatus.NOT_FOUND))
          .body(Map.of("error", "Error al eliminar hecho", "mensaje", e.getMessage()));
    }
  }

  //Obtener todos los hechos de una coleccion especifica
  @GetMapping("/colecciones/{colId}/hechos")
  public ResponseEntity<?> getHechos(@PathVariable String colId) {
    try {
      return ResponseEntity.ok(this.servicio.getHechos(colId));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar los hechos", "detalle", e.getMessage()));
    }
  }

  //Agregar una fuente de hechos a una coleccion
  @PostMapping("/colecciones/{colId}/fuentes") //Recibe el DTO de la fuente

  public ResponseEntity<?> agregarFuente(@PathVariable String colId, @RequestBody FuenteInputDTO dto) {
    try{
      //TODO deberia ser output
      FuenteInputDTO fuenteAgregada = servicio.agregarFuente(colId, dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(fuenteAgregada);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error","Error al agregar la fuene a la coleccion", "mensaje",e.getMessage()));
    }
  }


  //Borrar una fuente de una coleccion especifica
  @DeleteMapping("/colecciones/{colId}/fuentes/{fuenteId}")
  public ResponseEntity<?> eliminarFuente(@PathVariable String colId, @PathVariable Long fuenteId) {
    try{
      servicio.eliminarFuenteDeColeccion(colId,fuenteId);
      return ResponseEntity.ok(Map.of("mensaje", "Fuente borrada correctamente"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error","Error al eliminar la fuente de la coleccion", "mensaje", e.getMessage()));
    }

  }
  //Aprobar una solicitud de eliminación por id
  @PostMapping("/solicitudes-eliminacion/{id}/aprobar")
  public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(this.servicio.aprobarSolicitud(id));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar la solicitud", "detalle", e.getMessage()));
    }
  }

  //Rechazar solicitud de eliminacion por id
  @PostMapping("/solicitudes-eliminacion/{id}/denegar")
  public ResponseEntity<?> denegarSolicitud(@PathVariable Long id, @RequestBody SolicitudInputDTO dto){
    try {
      return ResponseEntity.ok(this.servicio.denegarSolicitud(id));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar la solicitud", "detalle", e.getMessage()));
    }

  }

  @PutMapping("/colecciones/{colId}/consenso")
  public ResponseEntity<?> modificarTipoAlgoritmoConsenso(@PathVariable String id, @RequestBody TipoAlgoritmoDeConsenso tipoAlgoritmo){
    try {
      return ResponseEntity.ok(this.servicio.modificarTipoAlgoritmoConsenso(tipoAlgoritmo, id));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar la coleccion", "detalle", e.getMessage()));
    }

  }
}


