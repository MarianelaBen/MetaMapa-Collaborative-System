package ar.utn.ba.ddsi.Metamapa.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String mostrarPanelControl(Model model, RedirectAttributes redirectAttributes) {
        List<ColeccionDTO> colecciones = this.coleccionService.getColecciones();
        model.addAttribute("titulo", "Panel de Control");
        model.addAttribute("colecciones", colecciones);
        return "administrador/panelControl";
    }

    @GetMapping("/gestor-solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String mostrarGestorSolicitudes(Model model, RedirectAttributes redirectAttributes) {
        List<SolicitudDTO> solicitudes = this.solicitudService.getSolicitudes();
        model.addAttribute("titulo", "Gestor de Solicitudes");
        model.addAttribute("solicitudes", solicitudes);
        return "administrador/gestorSolicitudes";
    }

    @GetMapping("/gestor-hechos")
    @PreAuthorize("hasAnyRole('ADMIN')")
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
      return "redirect:/administrador/importadorArchivosCSV";
    }
    //para pruebas
    System.out.println("llego CSV: " + archivo.getOriginalFilename());

    redirect.addFlashAttribute("mensaje", "Archivo subido correctamente. ");
    return "redirect:/administrador/importadorArchivosCSV";
  }

}
