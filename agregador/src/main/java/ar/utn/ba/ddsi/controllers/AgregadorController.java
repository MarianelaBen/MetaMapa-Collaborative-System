package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IConsensoService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import ar.utn.ba.ddsi.services.impl.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class AgregadorController {

  private final IAgregadorService agregadorService;
  private final ISolicitudService solicitudService;
  private final IFuenteRepository fuenteRepository;
  private final IConsensoService consensoService;

  public AgregadorController(IAgregadorService agregadorService, ISolicitudService solicitudService, IFuenteRepository fuenteRepository, IConsensoService consensoService){
    this.agregadorService = agregadorService;
    this.solicitudService = solicitudService;
    this.fuenteRepository = fuenteRepository;
    this.consensoService = consensoService;
  }


  @GetMapping("/hechos")
  public ResponseEntity<?> getHechos(Set<Fuente> fuentes){
    try{
      return ResponseEntity.ok(this.agregadorService.obtenerTodosLosHechos(fuentes));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error","Hechos no encontrados","mensaje", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al buscar los hechos", "mensaje" , e.getMessage()));
    }
  }

  /*public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }*/ //Porque esta aca?? //TODO BORRAR

  @GetMapping("/colecciones/{coleccionId}/hechos")
  public ResponseEntity<?> getHechosPorColeccion(@PathVariable String coleccionId, @RequestParam(value = "modo", defaultValue = "IRRESTRICTA") String modoStr) { //valor predeterminado IRRESTRICTA por si no se especifica nada de cuial se quiere usar
    try{
      //TODO chequear si es valido el metodo de navegacion
      System.out.println("Valores enum: " + Arrays.toString(TipoDeModoNavegacion.values()));
      String modoLimpio = modoStr.trim();

      TipoDeModoNavegacion modo = Arrays.stream(TipoDeModoNavegacion.values())
          .filter(m -> m.name().equalsIgnoreCase(modoLimpio))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Modo inválido: " + modoLimpio));

      return ResponseEntity.ok(agregadorService.obtenerHechosPorColeccion(coleccionId, modo));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error","Coleccion no encontrada","mensaje", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error","Error al obtener los hechos","mensaje", e.getMessage()));
    }
  }

  //navegacion filtrada sobre una coleccion
  @GetMapping("/colecciones/{coleccionId}/filtrados")
  public ResponseEntity<?> getHechosFiltrados(@PathVariable String coleccionId,
                                                 @RequestParam(required = false) String categoria,
                                                 @RequestParam(required = false) String fechaDesde,
                                                 @RequestParam(required = false) String fechaHasta) {
    try {
      return ResponseEntity.ok(
          agregadorService.obtenerHechosFiltrados(coleccionId, categoria, fechaDesde, fechaHasta)
      );
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error","Coleccion no encontrada","mensaje", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error","Error al filtrar hechos","mensaje", e.getMessage()));
    }
  }

  @PostMapping("/solicitudes")
  public ResponseEntity<?> crearSolicitudDeEliminacion(@RequestBody SolicitudDeEliminacion solicitud) {
    try {
      solicitudService.crearSolicitud(solicitud);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error","Solicitud inválida","mensaje", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error","Error al crear la solicitud","mensaje", e.getMessage()));
    }
  }

  @PostMapping("/fuentes")
  public ResponseEntity<?> guardarFuente(@RequestBody FuenteInputDTO dto) {
    try {
      if (dto.getUrl() == null || dto.getUrl().isBlank() || dto.getTipo() == null) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "Datos inválidos", "mensaje", "url y tipo son obligatorios"));
      }

      Fuente fuente = new Fuente(dto.getUrl(), dto.getTipo());
      Fuente guardada = fuenteRepository.save(fuente);

      return ResponseEntity.status(HttpStatus.CREATED).body(guardada);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al guardar la fuente", "mensaje", e.getMessage()));
    }
  }

  @GetMapping("/fuentes")
  public ResponseEntity<?> obtenerFuentes() {
    try {
      return ResponseEntity.ok(fuenteRepository.findAll());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al obtener las fuentes", "mensaje", e.getMessage()));
    }
  }

  @PostMapping("/algoritmo")
  public ResponseEntity<?> aplicarAlgoritmo() {
    try {
      consensoService.aplicarAlgoritmoDeConsenso();
      return ResponseEntity.ok("Algoritmo aplicado");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error al aplicar el algoritmo", "mensaje", e.getMessage()));
    }
  }
}
