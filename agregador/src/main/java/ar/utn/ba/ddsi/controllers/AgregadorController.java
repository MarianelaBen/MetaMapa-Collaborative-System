package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.impl.AgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class AgregadorController {

  private final IAgregadorService agregadorService;

  public AgregadorController(IAgregadorService agregadorService){
    this.agregadorService = agregadorService;
  }


  @GetMapping("/hechos")
  public List<HechoOutputDTO> getHechos(){
    return agregadorService.obtenerTodosLosHechos();
  }

  @GetMapping("/colecciones/{coleccionId}/hechos")
  public List<HechoOutputDTO> getHechosPorColeccion(@PathVariable Long coleccionId,
      @RequestParam(value = "modo", defaultValue = "IRRESTRICTA") String modoStr) { //valor predeterminado IRRESTRICTA por si no se especifica nada de cuial se quiere usar

    TipoDeModoNavegacion modo = TipoDeModoNavegacion.valueOf(modoStr);
    return agregadorService.obtenerHechosPorColeccion(coleccionId, modo);
  }
}
