package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {
  private final ColeccionService coleccionService;

  @GetMapping
  public String listarColecciones(Model model){
    List<ColeccionDTO> colecciones = this.generarColeccionesEjemplo(); //acá debería llamar al ColeccionService TODO implementar ColeccionService
    model.addAttribute("colecciones", colecciones);
    model.addAttribute("titulo", "Explorador de Colecciones");
    model.addAttribute("descripcion", "Navega por las diferentes colecciones de hechos disponibles en esta instancia de MetaMapa. Cada colección contiene información organizada temáticamente y geográficamente.");
    model.addAttribute("totalColecciones", colecciones.size());
    return "hechosYColecciones/exploradorColecciones";
  }

  //método para generar coleccion de ejemplo
  public static List<ColeccionDTO> generarColeccionesEjemplo() {
    return Arrays.asList(
        new ColeccionDTO(
            "Incendios forestales en Argentina 2025",
            "Monitoreo de incendios forestales ocurridos durante el año 2025 en territorio argentino. Datos actualizados desde múltiples fuentes oficiales y reportes ciudadanos. · 892 hechos · Última actualización: hace 30 minutos",
            Arrays.asList(
                new HechoDTO("IF-001", "Incendio en provincia A"),
                new HechoDTO("IF-002", "Incendio en provincia B")
            )
        ),

        new ColeccionDTO(
            "Desapariciones vinculadas a crímenes de odio",
            "Registro de casos de desapariciones forzadas relacionadas con crímenes de odio en Argentina. Incluye información sobre víctimas, fechas y ubicaciones de los últimos reportes. · 1,247 hechos · Última actualización: hace 2 horas",
            Arrays.asList(
                new HechoDTO("DV-001", "Desaparición - caso 1"),
                new HechoDTO("DV-002", "Desaparición - caso 2")
            )
        ),

        new ColeccionDTO(
            "Víctimas de muertes viales en Argentina",
            "Base de datos de accidentes de tránsito fatales en rutas y calles de Argentina. Información recopilada para análisis de seguridad vial y prevención. · 3,456 hechos · Última actualización: hace 1 día",
            Arrays.asList(
                new HechoDTO("MV-001", "Accidente - ruta X"),
                new HechoDTO("MV-002", "Accidente - calle Y")
            )
        ),

        new ColeccionDTO(
            "Desastres Naturales",
            "Registro histórico de eventos climáticos extremos, terremotos, inundaciones y otros desastres naturales que han afectado la región. · 678 hechos · Última actualización: hace 3 horas",
            Arrays.asList(
                new HechoDTO("DN-001", "Inundación - zona 1"),
                new HechoDTO("DN-002", "Terremoto - localidad 2")
            )
        ),

        new ColeccionDTO(
            "Personas asesinadas por el estado",
            "Documentación de casos de violencia institucional y abusos por parte de fuerzas de seguridad en Argentina. · 234 hechos · Última actualización: hace 5 horas",
            Arrays.asList(
                new HechoDTO("PAE-001", "Caso institucional 1"),
                new HechoDTO("PAE-002", "Caso institucional 2")
            )
        ),

        new ColeccionDTO(
            "Incendios forestales en España",
            "Datos sobre incendios forestales en territorio español, integrados desde fuentes oficiales europeas para análisis comparativo regional. · 445 hechos · Última actualización: hace 2 días",
            Arrays.asList(
                new HechoDTO("IF-ES-001", "Incendio - región ES1"),
                new HechoDTO("IF-ES-002", "Incendio - región ES2")
            )
        )
    );
  }
}
