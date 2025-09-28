package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/hechos")
@RequiredArgsConstructor
public class HechoController {
  private final HechoService hechoService;

  @GetMapping("/{id}")
  public String verDetalleHecho(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
    try{
      HechoDTO hecho =
          new HechoDTO(
              "Incendio forestal activo en Parque Nacional Los Glaciares",
              "Incendio de gran magnitud detectado en el sector norte del parque. Las llamas avanzan sobre zona de bosque nativo y requieren coordinación de brigadas aéreas y terrestres.",
              "Incendio forestal",
              LocalDateTime.of(2025, 8, 12, 9, 15),
              "Santa Cruz"
          );
          model.addAttribute("hecho", hecho);
          model.addAttribute("titulo", "Hecho " + hecho.getTitulo());

      return "hechosYColecciones/detalleHecho";
    }
    catch(NotFoundException ex){
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }
}

  /*
  public static List<HechoDTO> generarHechoEjemplo(){
    return Arrays.asList(
    new HechoDTO(
        "Incendio forestal activo en Parque Nacional Los Glaciares",
        "Incendio de gran magnitud detectado en el sector norte del parque. Las llamas avanzan sobre zona de bosque nativo y requieren coordinación de brigadas aéreas y terrestres.",
        "Incendio forestal",
        LocalDateTime.of(2025, 8, 12, 9, 15),
        "Santa Cruz"
      ),
        new HechoDTO(
            "Accidente múltiple en Ruta Nacional 9",
            "Colisión múltiple involucrando cuatro vehículos en el km 847, con varios heridos y corte parcial de la calzada. Brigadas de emergencia en el lugar.",
            "Accidente vial",
            LocalDateTime.of(2025, 8, 15, 16, 40),
            "Santa Fe"
        )
    );

  }

}
*/
