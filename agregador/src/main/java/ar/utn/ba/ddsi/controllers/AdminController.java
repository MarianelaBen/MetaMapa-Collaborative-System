/*package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.services.IColeccionService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private IColeccionService coleccionService;

  @Autowired
  private ISolicitudService solicitudService;


  @PostMapping("/colecciones")
  public void crearColeccion(@RequestBody Coleccion coleccion) {
   coleccionService.crearColeccion(coleccion);
  }

  @GetMapping("/colecciones/{id}")
  public Coleccion obtenerColeccion(@PathVariable String id) {
    return coleccionService.findById(id);
  }

  @PutMapping("/colecciones/{id}")
  public void actualizarColeccion(@PathVariable Long id, @RequestBody ColeccionDTO dto) {  }

  @DeleteMapping("/colecciones/{id}")
  public void eliminarColeccion(@PathVariable Long id) {  }

  // Obtener todos los hechos de una colección
  @GetMapping("/colecciones/{id}/hechos")
  public List<HechoOutputDTO> hechosDeColeccion(@PathVariable Long id) { return null; }

  // Modificar algoritmo de consenso
  @PutMapping("/colecciones/{id}/algoritmo-consenso")
  public void modificarAlgoritmo(@PathVariable Long id, @RequestParam String tipo) { }

  // Agregar fuente a colección
  @PostMapping("/colecciones/{id}/fuentes")
  public void agregarFuente(@PathVariable Long id, @RequestBody FuenteDTO fuente) { }

  // Quitar fuente de colección
  @DeleteMapping("/colecciones/{id}/fuentes/{fuenteId}")
  public void quitarFuente(@PathVariable Long id, @PathVariable Long fuenteId) {  }

  // Aprobar solicitud
  @PostMapping("/solicitudes/{id}/aprobar")
  public void aprobarSolicitud(@PathVariable Long id) {  }

  // Rechazar solicitud
  @PostMapping("/solicitudes/{id}/rechazar")
  public void rechazarSolicitud(@PathVariable Long id) {  }
}*/

