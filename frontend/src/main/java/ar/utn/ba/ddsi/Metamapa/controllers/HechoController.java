package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/hechos")
@RequiredArgsConstructor
public class HechoController {
  private final HechoService hechoService;

  @GetMapping("/{id}")
  public String verDetalleHecho(@PathVariable Long id,
                                Model model, RedirectAttributes redirectAttributes){
    try{
      // HechoDTO hecho = mockHecho(id);
      HechoDTO hecho = hechoService.getHechoPorId(id);

      List<String> nombresMultimedia =
          hecho.getIdContenidoMultimedia() == null ? List.of()
              : hecho.getIdContenidoMultimedia().stream()
              .map(HechoController::filenameFromPath)
              .toList();

          model.addAttribute("hecho", hecho);
          model.addAttribute("nombresMultimedia", nombresMultimedia);
          model.addAttribute("titulo", "Hecho " + hecho.getTitulo());

      return "hechosYColecciones/detalleHecho";
    }
    catch(NotFoundException ex){
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }

  @GetMapping("/nuevo")
  public String verFormulario(Model model) {
    model.addAttribute("titulo","Subir hecho");
    model.addAttribute("hecho", new HechoDTO(
        null,
        null,
        null,
        null,
        null, null
    ));
    model.addAttribute("categorias", List.of("Incendio forestal", "Accidente vial", "Inundación"));
    model.addAttribute("localidades", List.of("CABA", "La Plata", "Rosario"));

  return "hechosYColecciones/formularioHecho";
  }

  @PostMapping("/nuevo")
  public String procesarFormulario(@ModelAttribute("hecho") HechoDTO hecho,
                                        @RequestParam("fecha") String fecha,
                                        @RequestParam("hora") String hora,
                                        @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia,
                                        RedirectAttributes redirect, Model model){

    //si algun campo obligatorio no esta, que tire error
    boolean error = false;
    if (hecho.getTitulo() == null || hecho.getTitulo().isBlank()) { error = true; }
    if (hecho.getCategoria() == null || hecho.getCategoria().isBlank()) { error = true; }
    if (hecho.getDescripcion() == null || hecho.getDescripcion().isBlank()) { error = true; }
    if (hecho.getProvincia() == null || hecho.getProvincia().isBlank()) { error = true; }
    if (fecha == null || fecha.isBlank() || hora == null || hora.isBlank()) { error = true; }

    try {
      // convierto la hora y fecha que vienen separados desde el formulario, a LocalDateTime para el DTO
      if (!error) {
        LocalDate d = LocalDate.parse(fecha);
        LocalTime t = LocalTime.parse(hora);
        hecho.setFechaAcontecimiento(LocalDateTime.of(d, t));
      }
    } catch (Exception e) {
      error = true;
    }

    if (error) { //esto es si ya se mando el formulario y por alguna razon llega un campo obligatorio vacio
      // si hay error que vuelva a cargar el form y le pase al model los mismos datos harcodeados
      model.addAttribute("titulo", "Subir hecho");
      model.addAttribute("categorias", List.of("Incendio forestal", "Accidente vial", "Inundación"));
      model.addAttribute("localidades", List.of("CABA", "La Plata", "Rosario"));
      model.addAttribute("error", "Competar los campos obligatorios.");
      return "hechosYColecciones/formularioHecho";
    }

    if (multimedia != null ) { //esto es un log para ver si se estan subiendo, por que en la pantalla no aparece
      for (MultipartFile file : multimedia) {
        if (file != null && !file.isEmpty()) {
          System.out.println("Llego el archivo: " + file.getOriginalFilename()); //si subiste un archivo, por consola debe aparecer el nombre
        }
      }
    }

    HechoDTO creado = this.hechoService.subirHecho(hecho, multimedia);
    redirect.addFlashAttribute("mensaje", "Se envio correctamente el Hecho");
    redirect.addFlashAttribute("tipoMensaje", "success"); //mensaje de que esta ok

    return "redirect:/hechos/nuevo";

  }

  @GetMapping("/{id}/editar")
  // @PreAuthorize("hasAnyRole('CONTRIBUYENTE') and hasAnyAuthority('EDITAR_HECHO_PROPIO')")
  @PreAuthorize("permitAll()") // ← TEMPORAL para probar
  public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {

      HechoDTO hecho = hechoService.getHechoPorId(id);
      List<String> categorias = hechoService.getCategorias();

      List<String> nombresMultimedia =
          (hecho.getIdContenidoMultimedia() == null) ? List.of()
              : hecho.getIdContenidoMultimedia().stream()
              .map(HechoController::filenameFromPath)
              .toList();

      model.addAttribute("nombresMultimedia", nombresMultimedia);
      model.addAttribute("hecho", hecho);
      model.addAttribute("categorias", categorias);
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      return "contribuyente/editorHechos";

    } catch (NotFoundException ex) {
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }

  @PostMapping("/{id}/editar")
  @PreAuthorize("hasAnyRole('CONTRIBUYENTE') and hasAnyAuthority('EDITAR_HECHO_PROPIO')")
  public String procesarEdicion(@PathVariable Long id,
                                @ModelAttribute("hecho") HechoDTO hecho,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia,
                                @RequestParam(name = "replaceMedia", defaultValue = "false") boolean replaceMedia,
                                RedirectAttributes redirect,
                                Model model) {

    boolean error = false;

    if (hecho.getTitulo() == null || hecho.getTitulo().isBlank()) { error = true;}
    if (hecho.getCategoria() == null || hecho.getCategoria().isBlank()) { error = true; }
    if (hecho.getDescripcion() == null || hecho.getDescripcion().isBlank()) { error = true; }
    if (hecho.getProvincia() == null || hecho.getProvincia().isBlank()) { error = true; }
    if (fecha == null || fecha.isBlank() || hora == null || hora.isBlank()) { error = true; }

    try {
      if (!error) {
        LocalDate d = LocalDate.parse(fecha);
        LocalTime t = LocalTime.parse(hora);
        hecho.setFechaAcontecimiento(LocalDateTime.of(d, t));
      }
    } catch (Exception e) {
      error = true;
    }

    if (error) {
      model.addAttribute("categorias", hechoService.getCategorias());
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      model.addAttribute("errorMsg", "Hay campos obligatorios sin completar.");
      return "contribuyente/editorHechos";
    }

    hecho.setId(id);
    hechoService.actualizarHecho(id, hecho, multimedia, replaceMedia);

    redirect.addFlashAttribute("mensaje", "Hecho actualizado correctamente");
    redirect.addFlashAttribute("tipoMensaje", "success");
    return "redirect:/hechos/" + id;
  }

  // metodo usado en editorHechos (para el contenido multimedia)
  private static String filenameFromPath(String p) {
    if (p == null) return "";
    int slash = p.lastIndexOf('/');
    int back  = p.lastIndexOf('\\');
    int idx = Math.max(slash, back);
    return idx >= 0 ? p.substring(idx + 1) : p;
  }

    @PostMapping("/{id}/sumarVista")
    public String sumarVistaHecho(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes){
        try {
            hechoService.sumarVistaHecho(id);
            redirectAttributes.addFlashAttribute("mensaje", "Se sumó una vista al hecho exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/detalleHecho/" + id;
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/detalleHecho/" + id;
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/detalleHecho/" + id;
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("mensaje", "Error al sumar vista al hecho");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/detalleHecho/" + id;
        }
    }

  /* ===================== MOCK DEL HECHO ===================== */

  private List<String> categoriasMock() {
    return List.of("Incendio forestal", "Accidente vial", "Inundación");
  }

  private HechoDTO mockHecho(Long id) {
    HechoDTO dto = new HechoDTO(
        "Incendio forestal activo en Parque Nacional Los Glaciares",
        "Incendio de gran magnitud detectado en el sector norte del parque. Las llamas avanzan sobre zona de bosque nativo y requieren coordinación de brigadas aéreas y terrestres.",
        "Incendio forestal",
        LocalDateTime.of(2025, 8, 12, 9, 15),
        "Santa Cruz",
        id
    );
    return dto;
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
