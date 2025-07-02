package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.services.IAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/* TODO DTOs de consenso
import ar.utn.ba.ddsi.models.dtos.output.ConsensoResponseDTO;
import ar.utn.ba.ddsi.models.dtos.input.ConsensoDTO;*/


@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final IAdminService servicio;
  public AdminController(IAdminService servicio) {
    this.servicio = servicio;
  }

  //Obtener la lista completa de colecciones
  @GetMapping("/colecciones")
  public List<ColeccionOutputDTO> getColecciones() {
    return servicio.getColecciones();
  }

  //Crear una nueva colección.
  @PostMapping("/colecciones") //Recibe un DTO con los datos de la coleccion
  public ColeccionOutputDTO crearColeccion(@RequestBody ColeccionInputDTO dto) {
    return servicio.crearColeccion(dto);
  }


  //Actualiza coleccion por id
  @PutMapping("/colecciones/{id}")
  public ColeccionOutputDTO actualizarColeccion(
      @PathVariable String id,
      @RequestBody ColeccionInputDTO dto) {
    return servicio.actualizarColeccion(id, dto);
  }


  //Borra coleccion por su id
  @DeleteMapping("/colecciones/{id}")
  public ResponseEntity<Void> eliminarColeccion(@PathVariable String id) {
    servicio.eliminarColeccion(id);
    return ResponseEntity.noContent().build();//Si se elimina bien responde 204 no content
  }

  //Obtener todos los hechos de una coleccion especifica
  @GetMapping("/colecciones/{colId}/hechos")
  public List<HechoOutputDTO> getHechos(@PathVariable String colId) {
    return servicio.getHechos(colId);
  }

  //Agregar una fuente de hechos a una coleccion
  @PostMapping("/colecciones/{colId}/fuentes") //Recibe el DTO de la fuente
  public FuenteInputDTO agregarFuente(
      @PathVariable String colId,
      @RequestBody FuenteInputDTO dto) {
    return servicio.agregarFuente(colId, dto);
  }


  //Borrar una fuente de una coleccion especifica
  @DeleteMapping("/colecciones/{colId}/fuentes/{fuenteId}")
  public ResponseEntity<Void> eliminarFuente(@PathVariable String colId, @PathVariable Long fuenteId) {
    boolean eliminada = servicio.eliminarFuenteDeColeccion(colId,fuenteId);
    if (eliminada) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  //Aprobar una solicitud de eliminación por id
  @PostMapping("/solicitudes-eliminacion/{id}/aprobar")
  public SolicitudOutputDTO aprobarSolicitud(@PathVariable Long id) {
    return servicio.aprobarSolicitud(id);
  }

  //Rechazar solicitud de eliminacion por id
  @PostMapping("/solicitudes-eliminacion/{id}/denegar")
  public SolicitudOutputDTO denegarSolicitud(@PathVariable Long id, @RequestBody SolicitudInputDTO dto){
    return servicio.denegarSolicitud(id);
  }

  @PutMapping("/colecciones/{colId}/consenso")
  public void modificarTipoAlgoritmoConsenso(@PathVariable String id, @RequestBody TipoAlgoritmoDeConsenso tipoAlgoritmo){
    servicio.modificarTipoAlgoritmoConsenso(tipoAlgoritmo, id);
  }
}
