package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/contribuyente")
@RequiredArgsConstructor

public class ContribuyenteController {

  private final HechoService hechoService;
  // private final CategoriaService categoriaService;

  // Si usás Spring Security, mapeá tu user/principal a contribuyenteId.
  @GetMapping("/mis-hechos")
  public String verMisHechos(Model model
                             //,@AuthenticationPrincipal UsuarioActual usuario
                             ) {
    /* // Fallback de desarrollo: si no hay login, usar un ID fijo (ajusta a tu esquema)
    Long contribuyenteId = (usuario != null) ? usuario.getId() : 100L; */

    List<HechoDTO> hechos = hechoService.listarHechosDeContribuyente(contribuyenteId);
    model.addAttribute("hechos", hechos);
    model.addAttribute("categorias", categoriaService.listarCategorias());
    model.addAttribute("titulo", "Mis Hechos");
    model.addAttribute("descripcion", "Gestiona los hechos que has reportado. Puedes editarlos durante los primeros 7 días luego de su publicación.");

    return "contribuyente/misHechos";
  }
}
}
