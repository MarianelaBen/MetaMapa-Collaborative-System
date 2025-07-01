package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
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

  @GetMapping("/colecciones")
  public List<ColeccionOutputDTO> getColecciones() {
    return servicio.getColecciones();
  }

  @PostMapping("/colecciones")
  public ColeccionOutputDTO crearColeccion(@RequestBody ColeccionInputDTO dto) {
    return servicio.crearColeccion(dto);
  }

  @PutMapping("/colecciones/{id}")
  public ResponseEntity<ColeccionOutputDTO> actualizarColeccion(
      @PathVariable Long id,
      @RequestBody ColeccionInputDTO dto) {
    return servicio.actualizarColeccion(id, dto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/colecciones/{id}")
  public ResponseEntity<Void> eliminarColeccion(@PathVariable Long id) {
    servicio.eliminarColeccion(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/colecciones/{colId}/hechos")
  public List<HechoOutputDTO> getHechos(@PathVariable Long colId) {
    return servicio.getHechos(colId);
  }

  @PostMapping("/colecciones/{colId}/fuentes")
  public FuenteInputDTO agregarFuente(
      @PathVariable Long colId,
      @RequestBody FuenteInputDTO dto) {
    return servicio.agregarFuente(colId, dto);
  }

  @DeleteMapping("/colecciones/{colId}/fuentes/{fuenteId}")
  public ResponseEntity<Void> eliminarFuente(@PathVariable Long colId, @PathVariable Long fuenteId) {
    boolean eliminada = servicio.eliminarFuenteDeColeccion(colId,fuenteId);
    if (eliminada) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/solicitudes-eliminacion")
  public List<SolicitudOutputDTO> getSolicitudes(
      @RequestParam(required = false) String estado) {
    return servicio.getSolicitudes(estado);
  }

  @PostMapping("/solicitudes-eliminacion/{id}/aprobar")
  public SolicitudOutputDTO aprobarSolicitud(@PathVariable Long id) {
    return servicio.aprobarSolicitud(id);
  }

  @PostMapping("/solicitudes-eliminacion/{id}/denegar")
  public SolicitudOutputDTO denegarSolicitud(
      @PathVariable Long id,
      @RequestBody SolicitudInputDTO dto) {
    return servicio.denegarSolicitud(id);
  }

  /* TODO operaciones sobre consenso
  @PutMapping("/colecciones/{colId}/consenso")
  public ConsensoResponseDTO configurarConsenso(
      @PathVariable Long colId,
      @RequestBody ConsensoDTO dto) {
    return servicio.configurarConsenso(colId, dto);
  }

  @GetMapping("/colecciones/{colId}/consenso")
  public ResponseEntity<ConsensoResponseDTO> getConsenso(@PathVariable Long colId) {
    return servicio.getConsenso(colId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }*/
}
