package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {
  private final ColeccionService coleccionService;

  @GetMapping
  public String listarColecciones(Model model){
    List<ColeccionDTO> colecciones;

    //Agrego esto por que no levante el back
    try{
      colecciones = this.coleccionService.getColecciones();
    } catch (Exception e) {
      colecciones = List.of();
    }

    model.addAttribute("colecciones", colecciones);
    model.addAttribute("titulo", "Explorador de Colecciones");
    model.addAttribute("descripcion", "Navega por las diferentes colecciones de hechos disponibles en esta instancia de MetaMapa. Cada colección contiene información organizada temáticamente y geográficamente.");
    model.addAttribute("totalColecciones", colecciones.size());
    return "hechosYColecciones/exploradorColecciones";
  }

  @GetMapping("/{handle}")
  public String verDetalleColeccion(Model model, @PathVariable String handle, RedirectAttributes redirectAttributes){
    try{
      ColeccionDTO coleccion = this.coleccionService.getColeccionByHandle(handle);
      List<HechoDTO> hechosDeColeccion = this.coleccionService.getHechosDeColeccion(handle);
      model.addAttribute("coleccion", coleccion);
      model.addAttribute("hechos", hechosDeColeccion);
      model.addAttribute("titulo", "Coleccion " + coleccion.getHandle());
      return "hechosYColecciones/detalleColeccion";

    }catch(NotFoundException ex){
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }

@GetMapping("/nueva")
public String verFormulario(Model model) {
    model.addAttribute("titulo", "Crear nueva Coleccion");
    model.addAttribute("coleccion", new ColeccionDTO(null,null,null));
    model.addAttribute("algoritmos", List.of("Absoluta", "Mayoria Simple", "Multiples Menciones"));
    return "administrador/crearColeccion";
}

@PostMapping("/nueva")
public String crearColeccion(@ModelAttribute("coleccion") ColeccionDTO coleccion, RedirectAttributes redirect){
  try {

    //agregado ahora por el error
    if (coleccion.getHandle() == null || coleccion.getHandle().isBlank()) {
      coleccion.setHandle(makeHandle(coleccion.getTitulo()));
  }
    if (coleccion.getFuenteIds() == null) coleccion.setFuenteIds(Set.of());
    if (coleccion.getCriterioIds() == null) coleccion.setCriterioIds(Set.of());

    ColeccionDTO creada = coleccionService.crearColeccion(coleccion);
    redirect.addFlashAttribute("mensaje", "Coleccion creada: " + creada.getTitulo());
    return "redirect:/colecciones";
  } catch (Exception e) {
    //agrego esto tmb
    e.printStackTrace(); // para ver stack en consola
    redirect.addFlashAttribute("error", "Error al crear la coleccion.");
    return "redirect:/colecciones/nueva";
  }
}
//Raro
  private String makeHandle(String titulo) {
    if (titulo == null) return null;
    String h = titulo.toLowerCase()
        .replaceAll("\\s+", "-")         // espacios -> guion
        .replaceAll("[^a-z0-9-]", "")    // solo letras, numeros y guiones
        .replaceAll("-{2,}", "-")        // colapsa guiones repetidos
        .replaceAll("(^-|-$)", "");      // saca guion al inicio/fin
    if (h.isBlank()) {
      h = "coleccion-" + System.currentTimeMillis(); // fallback
    }
    return h;
  }

    @PostMapping("/{handle}/sumarVista")
    public String sumarVistaColeccion(@PathVariable String handle,
                                      @ModelAttribute("coleccion") ColeccionDTO coleccionDTO,
                                      RedirectAttributes redirectAttributes){
        try {
            coleccionService.sumarVistaColeccion(handle);
            redirectAttributes.addFlashAttribute("mensaje", "Se sumó una vista a la colección exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/exploradorColecciones";
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("mensaje", "Error al sumar vista a la colección");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        }
    }


}
