package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.InformeDeResultadosDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ResumenDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.Metamapa.services.AdminService;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import ar.utn.ba.ddsi.Metamapa.services.MetaMapaApiService;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ColeccionService coleccionService;
    private final SolicitudService solicitudService;
    private final HechoService hechoService;
    private final AdminService adminService;
    private final MetaMapaApiService metamapaApiService;

    @GetMapping("/panel-control")
    public String mostrarPanelControl(Model model, RedirectAttributes redirectAttributes) {
       // List<ColeccionDTO> colecciones = this.coleccionService.getColecciones();

      ResumenDTO resumen;
      try {
        resumen = metamapaApiService.getPanelDeControl();
      } catch (Exception ex) {
        System.err.println("[/admin/panel-control] " + ex.getMessage());
        resumen = new ResumenDTO(); // fallback
      }

        //var resumen = metamapaApiService.getPanelDeControl();

        model.addAttribute("resumen",resumen);
        model.addAttribute("titulo", "Panel de Control");
        model.addAttribute("colecciones", List.of());
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
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String verImportadorCSV(Model model) {
    model.addAttribute("titulo", "Importacion de hechos en archivos CSV");
    return "administrador/importadorArchivosCSV";
  }

  @PostMapping("/importarCSV")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String importarCSV(@RequestParam("archivo")MultipartFile archivo, RedirectAttributes redirect){
    if(archivo == null || archivo.isEmpty()) {
      redirect.addFlashAttribute("error", "Selecciona un archivo CSV");
      return "redirect:/admin/importarCSV";
    }
    try {
      InformeDeResultadosDTO informe = adminService.importarHechosCsv(archivo);
      redirect.addFlashAttribute("mensaje",
          "Importación OK: " + informe.getGuardadosTotales() + "/" + informe.getHechosTotales() + " guardados (" + informe.getTiempoTardado() + " ms)");
      redirect.addFlashAttribute("tipoMensaje", "success");
      redirect.addFlashAttribute("detalle",
          "Archivo: " + informe.getNombreOriginal() + " → " + informe.getGuardadoComo());
      redirect.addFlashAttribute("hechosTotales", informe.getHechosTotales());
      redirect.addFlashAttribute("guardadosTotales", informe.getGuardadosTotales());
      redirect.addFlashAttribute("tiempoTardado", informe.getTiempoTardado());
    } catch (WebClientResponseException e) {
      redirect.addFlashAttribute("mensaje",
          "El backend rechazó el CSV (" + e.getRawStatusCode() + "): " + e.getResponseBodyAsString());
      redirect.addFlashAttribute("tipoMensaje", "error");
    } catch (Exception e) {
      redirect.addFlashAttribute("mensaje", "No se pudo importar el CSV: " + e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "error");
    }
    //para pruebas
    System.out.println("llego CSV: " + archivo.getOriginalFilename());

    return "redirect:/admin/importarCSV";
  }


  @PostMapping("coleccion/{handle}/eliminar")
  @PreAuthorize("hasAnyRole('ADMIN')")
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

  @GetMapping("/coleccion/{handle}/editar")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String editarColeccionForm(@PathVariable String handle, Model model, RedirectAttributes redirect) {
    try {
      ColeccionDTO coleccion = coleccionService.getColeccionByHandle(handle);

      /*String criterioCsv = (coleccion.getCriterioIds() == null) ? "" :
          coleccion.getCriterioIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
      */
      model.addAttribute("titulo", "Editar Colección");
      model.addAttribute("coleccion", coleccion);
      //model.addAttribute("criterioIdsCsv", criterioCsv);
      model.addAttribute("algoritmosDisponibles", java.util.List.of("CONSENSO_ABSOLUTO", "MAYORIA_SIMPLE", "MULTIPLES_MENCIONES"));

      return "administrador/editarColeccion";

    } catch (NotFoundException e) {
      redirect.addFlashAttribute("mensaje", e.getMessage());
      return "redirect:/404";
    }
  }

  @PostMapping("/coleccion/{handle}/editar")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String editarColeccionGuardar(@PathVariable String handle,
                                       @ModelAttribute("coleccion") ColeccionDTO form,
                                       //@RequestParam(value = "criterioIdsCsv", required = false) String criterioIdsCsv,
                                       BindingResult binding,
                                       RedirectAttributes redirect) {
    if (binding.hasErrors()) {
      redirect.addFlashAttribute("mensaje", "Revisá los campos del formulario.");
      redirect.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/admin/coleccion/" + handle + "/editar";
    }
    try {
      /*
    }
      Set<Long> ids = new LinkedHashSet<>();
      if (criterioIdsCsv != null && !criterioIdsCsv.isBlank()) {
        for (String tok : criterioIdsCsv.split(",")) {
          String t = tok.trim();
          if (!t.isEmpty()) ids.add(Long.parseLong(t));
        }
      }
      form.setCriterioIds(ids);
  */
      // Si querés forzar que el handle de ruta sea el que manda:
      form.setHandle(handle);
      coleccionService.actualizarColeccion(handle, form);

      redirect.addFlashAttribute("mensaje", "Colección actualizada correctamente.");
      redirect.addFlashAttribute("tipoMensaje", "success");
      return "redirect:/admin/panel-control";

    } catch (ValidationException e) {
      redirect.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/admin/coleccion/" + handle + "/editar";

    } catch (NotFoundException e) {
      redirect.addFlashAttribute("mensaje", e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/admin/panel-control";
    }
  }

}
