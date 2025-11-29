package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.CategoriaDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.FuenteDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.MetaMapaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {
  private final ColeccionService coleccionService;
    private final MetaMapaApiService metaMapaApiService;

    @GetMapping
  public String listarColecciones(Model model){
    List<ColeccionDTO> colecciones;

    //Agrego esto por que no levante el back
    try{
      colecciones = this.coleccionService.getColecciones();
    } catch (Exception e) {
      colecciones = List.of();
    }

    model.addAttribute("colecciones", colecciones);
    model.addAttribute("titulo", "Explorador de Colecciones");
    model.addAttribute("descripcion", "Navega por las diferentes colecciones de hechos disponibles en esta instancia de MetaMapa. Cada colección contiene información organizada temáticamente y geográficamente.");
    model.addAttribute("totalColecciones", colecciones.size());
    return "hechosYColecciones/exploradorColecciones";
  }

    @GetMapping("/{handle}")
    public String verDetalleColeccion(
            Model model,
            @PathVariable String handle,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false, defaultValue = "false") Boolean modoCurado
    ) {
        try {
            ColeccionDTO coleccion = this.coleccionService.getColeccionByHandle(handle);
            List<CategoriaDTO> categorias = this.coleccionService.getCategorias();


            List<HechoDTO> hechosFiltrados = this.coleccionService.buscarHechos(
                    handle,
                    categoria,
                    fuente,
                    ubicacion,
                    keyword,
                    fechaDesde,
                    fechaHasta,
                    modoCurado
            );

            model.addAttribute("coleccion", coleccion);
            model.addAttribute("hechos", hechosFiltrados); // Pasamos la lista filtrada
            model.addAttribute("categorias", categorias);
            model.addAttribute("titulo", "Coleccion " + coleccion.getHandle());

            model.addAttribute("modoCurado", modoCurado);

            return "hechosYColecciones/detalleColeccion";

        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            return "redirect:/404";
        }
    }

@GetMapping("/nueva")
@PreAuthorize("hasAnyRole('ADMIN')")
public String verFormulario(Model model) {
  List<FuenteDTO> fuentes = metaMapaApiService.getFuentes();

  Map<String, Long> mapTipoId = fuentes.stream()
      .collect(Collectors.toMap(
          f -> f.getTipo().toUpperCase(),
          FuenteDTO::getId
      ));

  model.addAttribute("fuentesTipoId", mapTipoId);
  model.addAttribute("coleccion", new ColeccionDTO(null,null,null));
  model.addAttribute("titulo", "Crear nueva Coleccion");
    model.addAttribute("algoritmos", List.of("Absoluta", "Mayoria Simple", "Multiples Menciones"));
    return "administrador/crearColeccion";
}

@PostMapping("/nueva")
@PreAuthorize("hasAnyRole('ADMIN')")
public String crearColeccion(@ModelAttribute("coleccion") ColeccionDTO coleccion, @RequestParam(required = false) String fuenteTipos, RedirectAttributes redirect){
  try {

    //agregado ahora por el error
    if (coleccion.getHandle() == null || coleccion.getHandle().isBlank()) {
      coleccion.setHandle(makeHandle(coleccion.getTitulo()));
  }
    List<FuenteDTO> fuentes = metaMapaApiService.getFuentes();
    Map<String, Long> mapTipoId = fuentes.stream()
        .collect(Collectors.toMap(
            f -> f.getTipo().toUpperCase(),
            FuenteDTO::getId
        ));

    Set<Long> fuenteIds = new HashSet<>();

    if (fuenteTipos != null && !fuenteTipos.isBlank()) {
      for (String tipo : fuenteTipos.split(",")) {
        Long id = mapTipoId.get(tipo.toUpperCase());
        if (id != null) fuenteIds.add(id);
      }
    }

    coleccion.setFuenteIds(fuenteIds);
    if (coleccion.getCriterioIds() == null) coleccion.setCriterioIds(Set.of());

    ColeccionDTO creada = metaMapaApiService.crearColeccion(coleccion);
    redirect.addFlashAttribute("mensaje", "Coleccion creada: " + creada.getTitulo());
    return "redirect:/colecciones";
  } catch (Exception e) {
    //agrego esto tmb
    e.printStackTrace(); // para ver stack en consola
    redirect.addFlashAttribute("error", "Error al crear la coleccion.");
    return "redirect:/colecciones/nueva";
  }
}
//Raro
  private String makeHandle(String titulo) {
    if (titulo == null) return null;
    String h = titulo.toLowerCase()
        .replaceAll("\\s+", "-")         // espacios -> guion
        .replaceAll("[^a-z0-9-]", "")    // solo letras, numeros y guiones
        .replaceAll("-{2,}", "-")        // colapsa guiones repetidos
        .replaceAll("(^-|-$)", "");      // saca guion al inicio/fin
    if (h.isBlank()) {
      h = "coleccion-" + System.currentTimeMillis(); // fallback
    }
    return h;
  }

    @PostMapping("/{handle}/sumarVista")
    public String sumarVistaColeccion(@PathVariable String handle,
                                      @ModelAttribute("coleccion") ColeccionDTO coleccionDTO,
                                      RedirectAttributes redirectAttributes){
        try {
            coleccionService.sumarVistaColeccion(handle);
            redirectAttributes.addFlashAttribute("mensaje", "Se sumó una vista a la colección exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/exploradorColecciones";
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("mensaje", "Error al sumar vista a la colección");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/exploradorColecciones";
        }
    }

  @PostMapping("/actualizar-todas")
  public String actualizarTodasLasColecciones(RedirectAttributes ra) {
    try {
      // Llama al service del front, que a su vez llama al back
      coleccionService.actualizarTodasLasColecciones();

      ra.addFlashAttribute("mensaje", "Colecciones actualizadas correctamente.");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "No se pudieron actualizar las colecciones: " + e.getMessage());
    }

    // Volvés a la pantalla donde está la tabla
    return "redirect:/colecciones";
  }
}
