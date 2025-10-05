package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class LandingController {

  @GetMapping("/inicio")
  @PreAuthorize("hasAnyRole('CONTRIBUYENTE', 'VISUALIZADOR', 'ADMIN')")
  public String inicio(Model model){
    List<ColeccionDTO> coleccionesDestacadas = this.generarColeccionesDestacadasEjemplo();
    List<HechoDTO> hechosDestacados = this.generarHechosDestacadosEjemplo();
    model.addAttribute("titulo", "Inicio");
    model.addAttribute("coleccionesDestacadas", coleccionesDestacadas);
    model.addAttribute("hechosDestacados", hechosDestacados);
    return "landing/landing";
  }

  //método para generar coleccion destacadas de ejemplo
  public static List<ColeccionDTO> generarColeccionesDestacadasEjemplo() {
    return Arrays.asList(
        new ColeccionDTO(
            "Incendios forestales en Argentina 2025",
            "Monitoreo de incendios forestales ocurridos durante el año 2025 en territorio argentino. Datos actualizados desde múltiples fuentes oficiales y reportes ciudadanos.",
            "8"
        ),

        new ColeccionDTO(
            "Desapariciones vinculadas a crímenes de odio",
            "Registro de casos de desapariciones forzadas relacionadas con crímenes de odio en Argentina. Incluye información sobre víctimas, fechas y ubicaciones de los últimos reportes.",
            "9"
        ),

        new ColeccionDTO(
            "Víctimas de muertes viales en Argentina",
            "Base de datos de accidentes de tránsito fatales en rutas y calles de Argentina. Información recopilada para análisis de seguridad vial y prevención.",
            "10"
        ));
  }

  //método para generar coleccion destacadas de ejemplo
  public static List<HechoDTO> generarHechosDestacadosEjemplo() {
    return Arrays.asList(
        new HechoDTO(
            "Desborde del río Mendoza afecta viñedos",
            "Lluvias intensas provocaron desborde en canales de riego y anegamiento de viñedos en la zona productiva; daños preliminares en cultivos y caminos rurales.",
            "Riesgo hidrológico",
            LocalDateTime.of(2025, 6, 5, 7, 45),
            "Mendoza",
                7L
        ),
        new HechoDTO(
            "Explosión en planta de gas en zona industrial",
            "Explosión con incendio secundario en una planta de procesamiento; personal de emergencias controla el fuego y se realizan peritajes para determinar causas.",
            "Accidente industrial",
            LocalDateTime.of(2025, 8, 20, 2, 10),
            "Salta",
                8L
        ),
        new HechoDTO(
            "Derrame de hidrocarburos en costa patagónica",
            "Contaminación por vertido de hidrocarburos en la costa, afectando fauna marina y playas; se activan equipos de contención y remediación.",
            "Contaminación marina",
            LocalDateTime.of(2025, 5, 18, 13, 0),
            "Chubut",
                9L
        ));
  }
}
