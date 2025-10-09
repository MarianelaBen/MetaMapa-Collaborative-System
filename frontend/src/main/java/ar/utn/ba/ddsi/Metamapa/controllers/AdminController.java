package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ColeccionService coleccionService;
    private final SolicitudService solicitudService;
    private final HechoService hechoService;

    @GetMapping("/panel-control")
    public String mostrarPanelControl(Model model, RedirectAttributes redirectAttributes) {
        List<ColeccionDTO> colecciones = this.coleccionService.getColecciones();
        model.addAttribute("titulo", "Panel de Control");
        model.addAttribute("colecciones", colecciones);
        return "administrador/panelControl";
    }

    @GetMapping("/gestor-solicitudes")
    public String mostrarGestorSolicitudes(Model model, RedirectAttributes redirectAttributes) {
        List<SolicitudDTO> solicitudes = this.solicitudService.getSolicitudes();
        model.addAttribute("titulo", "Gestor de Solicitudes");
        model.addAttribute("solicitudes", solicitudes);
        return "administrador/gestorSolicitudes";
    }

    @GetMapping("/gestor-hechos")
    public String mostrarGestorHechos(Model model, RedirectAttributes redirectAttributes) {
        List<HechoDTO> hechos = this.hechoService.getHechos();
        model.addAttribute("titulo", "Gestor de Hechos");
        model.addAttribute("hechos", hechos);
        return "administrador/gestorHechos";
    }

  @GetMapping("/importarCSV")
  public String verImportadorCSV(Model model) {
    model.addAttribute("titulo", "Importacion de hechos en archivos CSV");
    return "administrador/importadorArchivosCSV";
  }

  @PostMapping("/importarCSV")
  public String importarCSV(@RequestParam("archivo")MultipartFile archivo, RedirectAttributes redirect){
    if(archivo == null || archivo.isEmpty()) {
      redirect.addFlashAttribute("error", "Selecciona un archivo CSV");
      return "redirect:/administrador/importadorArchivosCSV";
    }
    //para pruebas
    System.out.println("llego CSV: " + archivo.getOriginalFilename());

    redirect.addFlashAttribute("mensaje", "Archivo subido correctamente. ");
    return "redirect:/administrador/importadorArchivosCSV";
  }


  @PostMapping("coleccion/{handle}/eliminar")
  public String eliminarColeccion(@PathVariable String handle,
                                  @ModelAttribute("coleccion") ColeccionDTO coleccionDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes
  ) {
      try {
          coleccionService.eliminarColeccion(handle);
          redirectAttributes.addFlashAttribute("mensaje", "Colección eliminada exitosamente");
          redirectAttributes.addFlashAttribute("tipoMensaje", "success");
          // REDIRECT explícito a la ruta del panel
          return "redirect:/admin/panel-control";
      } catch (NotFoundException ex) {
          redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/panel-control";
      } catch (ValidationException e) {
          redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/panel-control";
      } catch (Exception e) {

          redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la colección");
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/panel-control";
      }
  }

  @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String aprobarSolicitud(@PathVariable Long id,
                                   @ModelAttribute("solicitud") SolicitudDTO solicitudDTO,
                                   Model model,
                                   RedirectAttributes redirectAttributes){
      try {
          solicitudService.aprobarSolicitud(id);
          redirectAttributes.addFlashAttribute("mensaje", "Solicitud aprobada exitosamente");
          redirectAttributes.addFlashAttribute("tipoMensaje", "success");
          // REDIRECT explícito a la ruta del panel
          return "redirect:/admin/gestor-solicitudes";
      } catch (NotFoundException ex) {
          redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/gestor-solicitudes";
      } catch (ValidationException e) {
          redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/gestor-solicitudes";
      }

  }

  @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN')")
  public String rechazarSolicitud(@PathVariable Long id,
                                 @ModelAttribute("solicitud") SolicitudDTO solicitudDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes){
      try {
          solicitudService.rechazarSolicitud(id);
          redirectAttributes.addFlashAttribute("mensaje", "Solicitud rechazada exitosamente");
          redirectAttributes.addFlashAttribute("tipoMensaje", "success");
          // REDIRECT explícito a la ruta del panel
          return "redirect:/admin/gestor-solicitudes";
      } catch (NotFoundException ex) {
          redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/gestor-solicitudes";
      } catch (ValidationException e) {
          redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
          redirectAttributes.addFlashAttribute("tipoMensaje", "error");
          return "redirect:/admin/gestor-solicitudes";
      }
  }

    @PostMapping("hecho/{id}/eliminar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String eliminarHecho(@PathVariable Long id,
                                    @ModelAttribute("hecho") HechoDTO hechoDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes
    ) {
        try {
            hechoService.eliminarHecho(id);
            redirectAttributes.addFlashAttribute("mensaje", "Hecho eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            // REDIRECT explícito a la ruta del panel
            return "redirect:/admin/gestor-hechos";
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/gestor-hechos";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/gestor-hechos";
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el hecho");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/gestor-hechos";
        }
    }
}
