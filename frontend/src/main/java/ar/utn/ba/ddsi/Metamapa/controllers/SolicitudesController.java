package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ar.utn.ba.ddsi.Metamapa.services.SolicitudService;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SolicitudesController {
    private final SolicitudService solicitudService;
    private final HechoService hechoService;


    @GetMapping("/solicitud/{hechoId}")
    public String mostrarFormulario(@PathVariable Long hechoId, Model model) {
        HechoDTO hecho = hechoService.getHechoPorId(hechoId);
        if (hecho == null) {
            model.addAttribute("error", "No se encontró el hecho con id " + hechoId);
            return "hechosYColecciones/mostrarHecho";
        }
        model.addAttribute("titulo", "Solicitar eliminación de hecho");
        model.addAttribute("hecho", hecho);


        if (!model.containsAttribute("justificacion")) {
            model.addAttribute("justificacion", "");
        }
        return "hechosYColecciones/solicitudEliminacion";
    }

    @GetMapping("/detalle/{solicitudId}")
    public String verDetalleSolicitud(@PathVariable Long solicitudId, Model model){
        SolicitudDTO solicitud = solicitudService.obtenerSolicitud(solicitudId);

        // PRIMERO chequeo si vino la solicitud
        if (solicitud == null) {
            model.addAttribute("error", "No se encontró la solicitud con id " + solicitudId);
            System.out.println("no se encontró la solicitud  con id " + solicitudId);
            return "administrador/gestorSolicitudes";
        }

        Long hechoId = solicitud.getHechoId();
        if (hechoId == null) {
            model.addAttribute("error", "La solicitud no contiene un hecho asociado (solicitud id " + solicitudId + ")");
            System.out.println("La solicitud no contiene un hecho asociado, solicitud id " + solicitudId + " hecho id " + hechoId );

            return "administrador/gestorSolicitudes";
        }

        HechoDTO hecho = hechoService.getHechoPorId(hechoId);
        if (hecho == null) {
            model.addAttribute("error", "No se encontró el hecho con id " + hechoId);
            System.out.println("No se encontró el hecho con id "+ hechoId) ;

            return "administrador/gestorSolicitudes";
        }

        model.addAttribute("titulo", "Solicitud de eliminación de hecho");
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("hecho", hecho);

        return "administrador/detalleSolicitud";
    }

    @PostMapping("/solicitud/{hechoId}/crear")
    public String crear(@PathVariable Long hechoId,
                        @RequestParam("justificacion") String justificacion,
                        Model model,
                        RedirectAttributes flash) {

        String texto = (justificacion == null) ? "" : justificacion.trim();
        if (texto.length() < 500) {
            // cargar el hecho para la vista de error
            HechoDTO hecho = hechoService.getHechoPorId(hechoId);
            model.addAttribute("titulo", "Solicitar eliminación de hecho");
            model.addAttribute("hecho", hecho);          // <-- importante
            model.addAttribute("hechoId", hechoId);
            model.addAttribute("justificacion", justificacion);
            model.addAttribute("error", "La justificación debe tener al menos 500 caracteres. Actualmente: " + texto.length());
            return "hechosYColecciones/solicitudEliminacion";
        }

        try {
            String solicitudId = solicitudService.crearSolicitudEliminacion(hechoId, texto);

            flash.addFlashAttribute("mensaje", "Solicitud creada (ID " + solicitudId + "). Quedó en estado PENDIENTE.");
            flash.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/solicitud/" + hechoId;

        } catch (Exception e) {
            // cargar el hecho para la vista de error
            HechoDTO hecho = hechoService.getHechoPorId(hechoId);
            model.addAttribute("titulo", "Solicitar eliminación de hecho");
            model.addAttribute("hecho", hecho);          // <-- importante
            model.addAttribute("hechoId", hechoId);
            model.addAttribute("justificacion", justificacion);
            model.addAttribute("error", "Error al crear la solicitud: " + e.getMessage());
            return "hechosYColecciones/solicitudEliminacion";
        }
    }



}
