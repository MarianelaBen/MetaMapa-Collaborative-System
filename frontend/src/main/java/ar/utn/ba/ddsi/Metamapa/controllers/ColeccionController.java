package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDateTime;
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
            "Monitoreo de incendios forestales ocurridos durante el año 2025 en territorio argentino. Datos actualizados desde múltiples fuentes oficiales y reportes ciudadanos.",
            Arrays.asList(
                new HechoDTO(
                    "Incendio forestal activo en Parque Nacional Los Glaciares",
                    "Incendio de gran magnitud detectado en el sector norte del parque. Las llamas avanzan sobre zona de bosque nativo y requieren coordinación de brigadas aéreas y terrestres.",
                    "Incendio forestal",
                    LocalDateTime.of(2025, 8, 12, 9, 15),
                    "Santa Cruz"
                ),
                new HechoDTO(
                    "Accidente múltiple en Ruta Nacional 9",
                    "Colisión múltiple involucrando cuatro vehículos en el km 847, con varios heridos y corte parcial de la calzada. Brigadas de emergencia en el lugar.",
                    "Accidente vial",
                    LocalDateTime.of(2025, 8, 15, 16, 40),
                    "Santa Fe"
                )
            )
        ),

        new ColeccionDTO(
            "Desapariciones vinculadas a crímenes de odio",
            "Registro de casos de desapariciones forzadas relacionadas con crímenes de odio en Argentina. Incluye información sobre víctimas, fechas y ubicaciones de los últimos reportes.",
            Arrays.asList(
                new HechoDTO(
                    "Accidente múltiple en Ruta Nacional 9",
                    "Colisión múltiple involucrando cuatro vehículos en el km 847, con varios heridos y corte parcial de la calzada. Brigadas de emergencia en el lugar.",
                    "Accidente vial",
                    LocalDateTime.of(2025, 8, 15, 16, 40),
                    "Santa Fe"
                ),
                new HechoDTO(
                    "Derrame químico en planta industrial",
                    "Reporte de un derrame de sustancias químicas en una planta ubicada en las afueras de la ciudad. Se evalúa riesgo de contaminación y se solicita evacuación preventiva de la zona cercana.",
                    "Accidente ambiental",
                    LocalDateTime.of(2025, 8, 10, 11, 5),
                    "Córdoba"
                )
            )
        ),

        new ColeccionDTO(
            "Víctimas de muertes viales en Argentina",
            "Base de datos de accidentes de tránsito fatales en rutas y calles de Argentina. Información recopilada para análisis de seguridad vial y prevención.",
            Arrays.asList(
                new HechoDTO(
                    "Inundaciones en barrios del Delta del Paraná",
                    "Crecida del río provocó desborde en barrios ribereños; anegamientos en calles y viviendas. Equipos de protección civil realizan evacuaciones y asistencia.",
                    "Inundación",
                    LocalDateTime.of(2025, 7, 28, 3, 0),
                    "Entre Ríos"
                ),
                new HechoDTO(
                    "Corte de energía masivo en Gran Buenos Aires",
                    "Falla en una subestación de distribución dejó sin servicio a amplios sectores de la zona metropolitana. Se trabaja en la normalización progresiva del suministro.",
                    "Corte de servicio",
                    LocalDateTime.of(2025, 9, 1, 20, 30),
                    "Buenos Aires"
                )
            )
        ),

        new ColeccionDTO(
            "Desastres Naturales",
            "Registro histórico de eventos climáticos extremos, terremotos, inundaciones y otros desastres naturales que han afectado la región.",
            Arrays.asList(
                new HechoDTO(
                    "Desborde del río Mendoza afecta viñedos",
                    "Lluvias intensas provocaron desborde en canales de riego y anegamiento de viñedos en la zona productiva; daños preliminares en cultivos y caminos rurales.",
                    "Riesgo hidrológico",
                    LocalDateTime.of(2025, 6, 5, 7, 45),
                    "Mendoza"
                ),
                new HechoDTO(
                    "Explosión en planta de gas en zona industrial",
                    "Explosión con incendio secundario en una planta de procesamiento; personal de emergencias controla el fuego y se realizan peritajes para determinar causas.",
                    "Accidente industrial",
                    LocalDateTime.of(2025, 8, 20, 2, 10),
                    "Salta"
                )
            )
        ),

        new ColeccionDTO(
            "Personas asesinadas por el estado",
            "Documentación de casos de violencia institucional y abusos por parte de fuerzas de seguridad en Argentina.",
            Arrays.asList(
                new HechoDTO(
                    "Explosión en planta de gas en zona industrial",
                    "Explosión con incendio secundario en una planta de procesamiento; personal de emergencias controla el fuego y se realizan peritajes para determinar causas.",
                    "Accidente industrial",
                    LocalDateTime.of(2025, 8, 20, 2, 10),
                    "Salta"
                ),
                new HechoDTO(
                    "Derrame de hidrocarburos en costa patagónica",
                    "Contaminación por vertido de hidrocarburos en la costa, afectando fauna marina y playas; se activan equipos de contención y remediación.",
                    "Contaminación marina",
                    LocalDateTime.of(2025, 5, 18, 13, 0),
                    "Chubut"
                )
            )
        ),

        new ColeccionDTO(
            "Incendios forestales en España",
            "Datos sobre incendios forestales en territorio español, integrados desde fuentes oficiales europeas para análisis comparativo regional.",
            Arrays.asList(
                new HechoDTO(
                    "Inundaciones en barrios del Delta del Paraná",
                    "Crecida del río provocó desborde en barrios ribereños; anegamientos en calles y viviendas. Equipos de protección civil realizan evacuaciones y asistencia.",
                    "Inundación",
                    LocalDateTime.of(2025, 7, 28, 3, 0),
                    "Entre Ríos"
                ),
                new HechoDTO(
                    "Corte de energía masivo en Gran Buenos Aires",
                    "Falla en una subestación de distribución dejó sin servicio a amplios sectores de la zona metropolitana. Se trabaja en la normalización progresiva del suministro.",
                    "Corte de servicio",
                    LocalDateTime.of(2025, 9, 1, 20, 30),
                    "Buenos Aires"
                )
            )
        )
    );
  }
}
