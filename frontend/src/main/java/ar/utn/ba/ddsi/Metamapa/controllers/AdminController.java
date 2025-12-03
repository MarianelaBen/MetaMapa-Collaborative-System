package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.*;
import ar.utn.ba.ddsi.Metamapa.services.AdminService;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import ar.utn.ba.ddsi.Metamapa.services.MetaMapaApiService;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String mostrarPanelControl(Model model, RedirectAttributes redirectAttributes) {

      ResumenDTO resumen;
      try {
        resumen = metamapaApiService.getPanelDeControl();
        List<ColeccionDTO> colecciones = this.coleccionService.getColecciones();
          model.addAttribute("resumen",resumen);
          model.addAttribute("titulo", "Panel de Control");
          model.addAttribute("colecciones", colecciones);
      } catch (Exception ex) {
        System.err.println("[/admin/panel-control] " + ex.getMessage());
        resumen = new ResumenDTO(); // fallback
      }

        return "administrador/panelControl";
    }

// En AdminController.java

    @GetMapping("/gestor-solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String mostrarGestorSolicitudes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {

            PaginaDTO<SolicitudDTO> resp = adminService.obtenerSolicitudesPaginado(page, size);


            long from = resp.getNumber() * (long) resp.getSize() + (resp.getNumberOfElements() > 0 ? 1 : 0);
            long to   = resp.getNumber() * (long) resp.getSize() + resp.getNumberOfElements();

            model.addAttribute("titulo", "Gestor de Solicitudes");
            model.addAttribute("solicitudes", resp.getContent());

            model.addAttribute("page", resp.getNumber());
            model.addAttribute("size", resp.getSize());
            model.addAttribute("totalPages", resp.getTotalPages());
            model.addAttribute("totalElements", resp.getTotalElements());
            model.addAttribute("hasPrev", !resp.isFirst());
            model.addAttribute("hasNext", !resp.isLast());
            model.addAttribute("prevPage", resp.getNumber() - 1);
            model.addAttribute("nextPage", resp.getNumber() + 1);
            model.addAttribute("from", from);
            model.addAttribute("to", to);

        } catch (Exception ex) {
            System.err.println("[/admin/gestor-solicitudes] " + ex.getMessage());
            model.addAttribute("error", "Error al cargar solicitudes: " + ex.getMessage());
        }

        return "administrador/gestorSolicitudes";
    }


    @GetMapping("/gestor-hechos")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String mostrarGestorHechos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int catPage,
            @RequestParam(defaultValue = "5") int catSize,
            Model model
    ) {

        PaginaDTO<HechoDTO> respHechos = adminService.obtenerHechosPaginado(page, size);

        long from = respHechos.getNumber() * (long) respHechos.getSize() + (respHechos.getNumberOfElements() > 0 ? 1 : 0);
        long to   = respHechos.getNumber() * (long) respHechos.getSize() + respHechos.getNumberOfElements();

        model.addAttribute("hechos", respHechos.getContent());
        model.addAttribute("page", respHechos.getNumber());
        model.addAttribute("size", respHechos.getSize());
        model.addAttribute("totalPages", respHechos.getTotalPages());
        model.addAttribute("totalElements", respHechos.getTotalElements());
        model.addAttribute("hasPrev", !respHechos.isFirst());
        model.addAttribute("hasNext", !respHechos.isLast());
        model.addAttribute("prevPage", respHechos.getNumber() - 1);
        model.addAttribute("nextPage", respHechos.getNumber() + 1);
        model.addAttribute("from", from);
        model.addAttribute("to", to);


        PaginaDTO<CategoriaDTO> respCat = adminService.obtenerCategoriasPaginado(catPage, catSize);

        model.addAttribute("categorias", respCat.getContent());

        model.addAttribute("catPage", respCat.getNumber());
        model.addAttribute("catSize", respCat.getSize());
        model.addAttribute("catTotalPages", respCat.getTotalPages());
        model.addAttribute("catTotalElements", respCat.getTotalElements());
        model.addAttribute("catHasPrev", !respCat.isFirst());
        model.addAttribute("catHasNext", !respCat.isLast());
        model.addAttribute("catPrevPage", respCat.getNumber() - 1);
        model.addAttribute("catNextPage", respCat.getNumber() + 1);

        model.addAttribute("titulo", "Gestor de Hechos");

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
      InformeDeResultadosDTO informe = this.metamapaApiService.importarHechosCsv(archivo);
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

    @DeleteMapping("/categorias/{id}/eliminar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String eliminarCategoria(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes
    ) {
        try {
            adminService.eliminarCategoria(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoria eliminada exitosamente");
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

            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la categoria");
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
      metamapaApiService.actualizarColeccion(handle, form);

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

    @PostMapping("/categorias/{id}/editar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String editarCategoria(@PathVariable Long id,
                                         @ModelAttribute("categoria") CategoriaDTO form,
                                         BindingResult binding,
                                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            redirect.addFlashAttribute("mensaje", "Revisá los campos del formulario.");
            redirect.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/gestor-hechos";
        }
        try {
            adminService.actualizarCategoria(id, form);

            redirect.addFlashAttribute("mensaje", "Categoria actualizada correctamente.");
            redirect.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/admin/gestor-hechos";

        } catch (ValidationException e) {
            redirect.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
            redirect.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/categorias/" + id + "/editar";

        } catch (NotFoundException e) {
            redirect.addFlashAttribute("mensaje", e.getMessage());
            redirect.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/gestor-hechos";
        }
    }

    @PostMapping("/categorias/nueva")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String crearCategoria(@ModelAttribute("categoria") CategoriaDTO categoria, RedirectAttributes redirect){
        try {

            CategoriaDTO creada = adminService.crearCategoria(categoria);
            redirect.addFlashAttribute("mensaje", "Categoria creada: " + creada.getNombre());
            return "redirect:/admin/gestor-hechos";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al crear la categoria.");
            return "redirect:/admin/gestor-hechos";
        }
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public String verDashboard(@RequestParam(defaultValue = "anio") String rango, Model model) {

        DashboardDTO stats = adminService.obtenerEstadisticas(rango);


        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalHechos", stats.getTotalHechos() != null ? stats.getTotalHechos() : 0);
        kpis.put("hechosVerificados", stats.getHechosVerificados() != null ? stats.getHechosVerificados() : 0);
        kpis.put("spamDetectado", stats.getSpamDetectado() != null ? stats.getSpamDetectado() : 0);
        kpis.put("porcentajeSpam", stats.getPorcentajeSpam() != null ? String.format("%.1f", stats.getPorcentajeSpam()) : "0.0");

        model.addAttribute("titulo", "Panel de Estadísticas");
        model.addAttribute("kpis", kpis);

        model.addAttribute("resumen", stats);

        model.addAttribute("datosCategorias", stats.getHechosPorCategoria() != null ? stats.getHechosPorCategoria() : Map.of());
        model.addAttribute("datosProvincias", stats.getHechosPorProvincia() != null ? stats.getHechosPorProvincia() : Map.of());
        model.addAttribute("datosHorarios", stats.getHechosPorHora() != null ? stats.getHechosPorHora() : Map.of());

        return "administrador/estadisticas";
    }

    @GetMapping("/exportar")
    public ResponseEntity<Resource> exportarDatos() {
        ByteArrayResource resource = adminService.exportarCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metamapa_stats.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

  @GetMapping("/solicitudes-edicion")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String verSolicitudesEdicionCreacion(Model model) {
    try {
      List<SolicitudEdicionCreacionDTO> solicitudes = solicitudService.getSolicitudesEdicionCreacion();

      model.addAttribute("titulo", "Solicitudes de Creación y Edición");
      model.addAttribute("solicitudes", solicitudes);

      long pendientesCreacion = solicitudes.stream().filter(s -> "CREACION".equals(s.getTipoSolicitud())).count();
      long pendientesEdicion = solicitudes.stream().filter(s -> "EDICION".equals(s.getTipoSolicitud())).count();

      model.addAttribute("countCreacion", pendientesCreacion);
      model.addAttribute("countEdicion", pendientesEdicion);

    } catch (Exception ex) {
      model.addAttribute("error", "No se pudieron cargar las solicitudes: " + ex.getMessage());
      model.addAttribute("solicitudes", List.of());
    }
    return "administrador/gestorSolicitudesEdicion";
  }

  @PostMapping("/solicitudes-edicion/{id}/aceptar")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String aceptarSolicitudEdicion(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      solicitudService.aceptarSolicitudEdicionCreacion(id);
      redirect.addFlashAttribute("mensaje", "Solicitud ACEPTADA correctamente.");
      redirect.addFlashAttribute("tipoMensaje", "success");
    } catch (Exception e) {
      redirect.addFlashAttribute("mensaje", "Error al aceptar: " + e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "error");
    }
    return "redirect:/admin/solicitudes-edicion";
  }

  @PostMapping("/solicitudes-edicion/{id}/rechazar")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public String rechazarSolicitudEdicion(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      solicitudService.rechazarSolicitudEdicionCreacion(id);
      redirect.addFlashAttribute("mensaje", "Solicitud RECHAZADA correctamente.");
      redirect.addFlashAttribute("tipoMensaje", "success"); // O warning visualmente
    } catch (Exception e) {
      redirect.addFlashAttribute("mensaje", "Error al rechazar: " + e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "error");
    }
    return "redirect:/admin/solicitudes-edicion";
  }
}
