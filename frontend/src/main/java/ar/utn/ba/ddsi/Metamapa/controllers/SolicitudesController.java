package ar.utn.ba.ddsi.Metamapa.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;

@Controller
@RequestMapping("/hechos/{hechoId}/solicitudes/eliminacion")
@RequiredArgsConstructor
public class SolicitudesController {
    private final SolicitudService solicitudService;


    @GetMapping("/nueva")
    public String mostrarFormulario(@PathVariable String hechoId, Model model) {
        model.addAttribute("titulo", "Solicitar eliminación de hecho");
        model.addAttribute("hechoId", hechoId);

        if (!model.containsAttribute("justificacion")) {
            model.addAttribute("justificacion", "");
        }
        return "solicitudes/eliminacion/crear";
    }


    @PostMapping("/crear")
    public String crear(@PathVariable String hechoId,
                        @RequestParam("justificacion") String justificacion,
                        Model model,
                        RedirectAttributes flash) {

        String texto = (justificacion == null) ? "" : justificacion.trim();
        if (texto.length() < 500) {
            model.addAttribute("titulo", "Solicitar eliminación de hecho");
            model.addAttribute("hechoId", hechoId);
            model.addAttribute("justificacion", justificacion);
            model.addAttribute("error", "La justificación debe tener al menos 500 caracteres. Actualmente: " + texto.length());
            return "solicitudes/eliminacion/crear";
        }

        try {

            String solicitudId = solicitudService.crearSolicitudEliminacion(hechoId, texto);

            flash.addFlashAttribute("mensaje", "Solicitud creada (ID "  + "). Quedó en estado PENDIENTE.");
            flash.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/hechos/" + hechoId;

        }catch (Exception e) {
            model.addAttribute("titulo", "Solicitar eliminación de hecho");
            model.addAttribute("hechoId", hechoId);
            model.addAttribute("justificacion", justificacion);
            model.addAttribute("error", "Error al crear la solicitud: " + e.getMessage());
            return "solicitudes/eliminacion/crear";
        }
    }
}
