package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.impl.AgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
