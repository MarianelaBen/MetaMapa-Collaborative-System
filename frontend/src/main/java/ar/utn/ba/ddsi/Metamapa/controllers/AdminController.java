package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;
import lombok.RequiredArgsConstructor;
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
        model.addAttribute("titulo", "Gestor de Hechos");
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

}
