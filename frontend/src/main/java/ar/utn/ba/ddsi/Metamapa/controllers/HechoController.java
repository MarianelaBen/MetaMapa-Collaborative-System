package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import lombok.RequiredArgsConstructor;
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
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/hechos")
@RequiredArgsConstructor
public class HechoController {
  private final HechoService hechoService;

  @GetMapping("/{id}")
  public String verDetalleHecho(@PathVariable Long id,
                                @RequestParam(name="chandle", required=false) String coleccionHandle,
                                @RequestParam(name="ctitulo", required=false) String coleccionTitulo,
                                Model model, RedirectAttributes redirectAttributes){
    try{
      // MOCK de breadcrumb
      if (coleccionHandle == null) coleccionHandle = "1";
      if (coleccionTitulo == null) coleccionTitulo = "Incendios forestales en Argentina 2025";

      // MOCK del hecho
      HechoDTO hecho = mockHecho(id);
          model.addAttribute("hecho", hecho);
          model.addAttribute("titulo", "Hecho " + hecho.getTitulo());
          model.addAttribute("coleccionHandle", coleccionHandle);
          model.addAttribute("coleccionTitulo", coleccionTitulo);

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
        null
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

    // TODO: aca hariamos el post de hecho
    redirect.addFlashAttribute("mensaje", "Se envio correctamente el Hecho");
    redirect.addFlashAttribute("tipoMensaje", "success"); //mensaje de que esta ok

    return "redirect:/hechos/nuevo";

  }

  @GetMapping("/{id}/editar")
  public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      // HechoDTO hecho = hechoService.obtenerPorId(id).orElseThrow(() -> new NotFoundException("Hecho", id.toString()));

      HechoDTO hecho = mockHecho(id);

      model.addAttribute("hecho", hecho);
      model.addAttribute("categorias", categoriasMock());
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      return "contribuyente/editorHechos";

    } catch (NotFoundException ex) {
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }

  @PostMapping("/{id}/editar")
  public String procesarEdicion(@PathVariable Long id,
                                @ModelAttribute("hecho") HechoDTO hecho,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia,
                                RedirectAttributes redirect,
                                Model model) {

    boolean error = false;

    if (hecho.getTitulo() == null || hecho.getTitulo().isBlank()) { error = true; }
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
      model.addAttribute("categorias", categoriasMock());
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      model.addAttribute("error", "Completá los campos obligatorios.");
      return "contribuyente/editorHechos";
    }

    // log de archivos por ahora
    if (multimedia != null) {
      for (MultipartFile file : multimedia) {
        if (file != null && !file.isEmpty()) {
          System.out.println("Archivo subido (edición): " + file.getOriginalFilename());
        }
      }
    }

    // TODO cuando conectemos con el backend: hechoService.actualizar(id, hecho, multimedia);

    redirect.addFlashAttribute("mensaje", "Hecho actualizado correctamente");
    redirect.addFlashAttribute("tipoMensaje", "success");
    return "redirect:/hechos/" + id; // vuelve al detalle
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
        "Santa Cruz"
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
