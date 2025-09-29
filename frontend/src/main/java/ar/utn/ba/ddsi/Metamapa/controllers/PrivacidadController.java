package ar.utn.ba.ddsi.Metamapa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacidadController {

  @GetMapping("/privacidad")
  public String privacidad(Model model) {
    model.addAttribute("titulo", "Información Legal & Privacidad");
    return "landing/privacidad";
  }
}
