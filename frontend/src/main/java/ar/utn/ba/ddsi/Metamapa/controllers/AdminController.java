package ar.utn.ba.ddsi.Metamapa.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrador")
@RequiredArgsConstructor
public class AdminController {

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
