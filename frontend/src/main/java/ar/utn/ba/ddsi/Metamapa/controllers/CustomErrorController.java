package ar.utn.ba.ddsi.Metamapa.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);

            if (statusCode == 404) {
                model.addAttribute("error", "Página no encontrada");
                model.addAttribute("message", "La URL que intentas visitar no existe o ha sido movida.");
            } else if (statusCode == 403) {
                model.addAttribute("error", "Acceso Denegado");
                model.addAttribute("message", "No tienes permisos para ver este recurso.");
            } else if (statusCode == 500) {
                model.addAttribute("error", "Error Interno");
                model.addAttribute("message", "Ocurrió un error inesperado en el servidor.");
            } else {
                model.addAttribute("error", "Error inesperado");
                model.addAttribute("message", "Algo salió mal.");
            }
        }

        return "error";
    }
}