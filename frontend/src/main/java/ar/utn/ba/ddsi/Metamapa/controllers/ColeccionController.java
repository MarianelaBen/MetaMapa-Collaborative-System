package ar.utn.ba.ddsi.Metamapa.controllers;

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
@PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('ADMIN_COLECCIONES')")
public String verFormulario(Model model) {
    model.addAttribute("titulo", "Crear nueva Coleccion");
    model.addAttribute("coleccion", new ColeccionDTO(null,null,null));
    model.addAttribute("algoritmos", List.of("Absoluta", "Mayoria Simple", "Multiples Menciones"));
    return "administrador/crearColeccion";
}

@PostMapping("/nueva")
@PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('ADMIN_COLECCIONES')")
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

  //método para generar coleccion de ejemplo
  public static List<ColeccionDTO> generarColeccionesEjemplo() {
    return Arrays.asList(
        new ColeccionDTO(
            "Incendios forestales en Argentina 2025",
            "Monitoreo de incendios forestales ocurridos durante el año 2025 en territorio argentino. Datos actualizados desde múltiples fuentes oficiales y reportes ciudadanos.",
            "2"
        ),

        new ColeccionDTO(
            "Desapariciones vinculadas a crímenes de odio",
            "Registro de casos de desapariciones forzadas relacionadas con crímenes de odio en Argentina. Incluye información sobre víctimas, fechas y ubicaciones de los últimos reportes.",
            "3"
        ),

        new ColeccionDTO(
            "Víctimas de muertes viales en Argentina",
            "Base de datos de accidentes de tránsito fatales en rutas y calles de Argentina. Información recopilada para análisis de seguridad vial y prevención.",
            "4"
        ),

        new ColeccionDTO(
            "Desastres Naturales",
            "Registro histórico de eventos climáticos extremos, terremotos, inundaciones y otros desastres naturales que han afectado la región.",
            "5"
        ),

        new ColeccionDTO(
            "Personas asesinadas por el estado",
            "Documentación de casos de violencia institucional y abusos por parte de fuerzas de seguridad en Argentina.",
            "6"
        ),

        new ColeccionDTO(
            "Incendios forestales en España",
            "Datos sobre incendios forestales en territorio español, integrados desde fuentes oficiales europeas para análisis comparativo regional.",
            "7"
        )
    );
  }
}
