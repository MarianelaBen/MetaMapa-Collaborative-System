package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.enums.Origen;
import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.*;
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

// En ColeccionController.java

    @GetMapping
    public String listarColecciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model
    ) {
        try {

            PaginaDTO<ColeccionDTO> pagina = this.coleccionService.getColeccionesPaginadas(page, size);


            long from = pagina.getNumber() * (long) pagina.getSize() + (pagina.getNumberOfElements() > 0 ? 1 : 0);
            long to   = pagina.getNumber() * (long) pagina.getSize() + pagina.getNumberOfElements();

            model.addAttribute("colecciones", pagina.getContent());
            model.addAttribute("titulo", "Explorador de Colecciones");
            model.addAttribute("descripcion", "Navega por las diferentes colecciones...");


            model.addAttribute("page", pagina.getNumber());
            model.addAttribute("size", pagina.getSize());
            model.addAttribute("totalPages", pagina.getTotalPages());
            model.addAttribute("totalElements", pagina.getTotalElements());
            model.addAttribute("hasPrev", !pagina.isFirst());
            model.addAttribute("hasNext", !pagina.isLast());
            model.addAttribute("prevPage", pagina.getNumber() - 1);
            model.addAttribute("nextPage", pagina.getNumber() + 1);
            model.addAttribute("from", from);
            model.addAttribute("to", to);

        } catch (Exception e) {

            model.addAttribute("colecciones", List.of());
            model.addAttribute("titulo", "Explorador de Colecciones");
            model.addAttribute("descripcion", "Hubo un error al cargar las colecciones.");
            model.addAttribute("totalPages", 0);
        }

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
            @RequestParam(required = false, defaultValue = "false") Boolean modoCurado,
            @RequestParam(defaultValue = "0") int page
    ) {
        try {
            ColeccionDTO coleccion = this.coleccionService.getColeccionByHandle(handle);
            List<CategoriaDTO> categorias = this.coleccionService.getCategorias();

            PaginaDTO<HechoDTO> pagina = this.coleccionService.buscarHechos(
                    handle, categoria, fuente, ubicacion, keyword, fechaDesde, fechaHasta, modoCurado, page, 12
            );

            model.addAttribute("coleccion", coleccion);
            model.addAttribute("categorias", categorias);
            model.addAttribute("titulo", "Coleccion " + coleccion.getHandle());
            model.addAttribute("modoCurado", modoCurado);
            model.addAttribute("hechos", pagina.getContent());
            model.addAttribute("paginaActual", pagina.getNumber());
            model.addAttribute("totalPaginas", pagina.getTotalPages());
            model.addAttribute("totalElementos", pagina.getTotalElements());

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

        List<CategoriaDTO> listaCategorias;
        try {
            listaCategorias = this.coleccionService.getCategorias();
        } catch (Exception e) {
            listaCategorias = List.of();
            e.printStackTrace();
        }

        model.addAttribute("fuentesTipoId", mapTipoId);


        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaOrigenes", Origen.values());

        model.addAttribute("coleccion", new ColeccionDTO(null, null, null));

        model.addAttribute("titulo", "Crear nueva Coleccion");
        model.addAttribute("algoritmos", List.of("Absoluta", "Mayoria Simple", "Multiples Menciones"));

        return "administrador/crearColeccion";
    }

    @PostMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String crearColeccion(
            @ModelAttribute("coleccion") ColeccionDTO coleccion,
            @RequestParam(required = false) String fuenteTipos,

            // --- CAPTURA DE PARAMETROS DEL FORMULARIO HTML ---
            // Arrays de Strings
            @RequestParam(value = "criterioTitulo[]", required = false) List<String> titulos,
            @RequestParam(value = "criterioDescripcion[]", required = false) List<String> descripciones,
            @RequestParam(value = "criterioCategoria[]", required = false) List<String> categorias,
            @RequestParam(value = "criterioOrigen[]", required = false) List<String> origenes,

            // Fechas (Thymeleaf envía strings yyyy-MM-dd, Spring los convierte a LocalDate)
            @RequestParam(value = "criterioFechaAcontecimientoDesde[]", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> faDesde,
            @RequestParam(value = "criterioFechaAcontecimientoHasta[]", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> faHasta,

            @RequestParam(value = "criterioFechaCargaDesde[]", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fcDesde,
            @RequestParam(value = "criterioFechaCargaHasta[]", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fcHasta,

            // Datos numéricos para Lugar
            @RequestParam(value = "criterioLugarLat[]", required = false) List<Double> lLat,
            @RequestParam(value = "criterioLugarLon[]", required = false) List<Double> lLon,
            @RequestParam(value = "criterioLugarRango[]", required = false) List<Integer> lRango,
            @RequestParam(value = "criterioLugarProvincia[]", required = false) List<String> lProvincia,


            RedirectAttributes redirect) {

        try {
            if (coleccion.getHandle() == null || coleccion.getHandle().isBlank()) {
                coleccion.setHandle(makeHandle(coleccion.getTitulo()));
            }

            List<FuenteDTO> fuentes = metaMapaApiService.getFuentes();
            Map<String, Long> mapTipoId = fuentes.stream()
                    .collect(Collectors.toMap(f -> f.getTipo().toUpperCase(), FuenteDTO::getId));

            Set<Long> fuenteIds = new HashSet<>();
            if (fuenteTipos != null && !fuenteTipos.isBlank()) {
                for (String tipo : fuenteTipos.split(",")) {
                    Long id = mapTipoId.get(tipo.toUpperCase());
                    if (id != null) fuenteIds.add(id);
                }
            }
            coleccion.setFuenteIds(fuenteIds);

            List<CriterioDTO> nuevosCriterios = new ArrayList<>();


            if (titulos != null) titulos.forEach(t -> nuevosCriterios.add(new CriterioDTO("TITULO", t)));
            if (descripciones != null) descripciones.forEach(d -> nuevosCriterios.add(new CriterioDTO("DESCRIPCION", d)));
            if (categorias != null) categorias.forEach(c -> nuevosCriterios.add(new CriterioDTO("CATEGORIA", c)));
            if (origenes != null) origenes.forEach(o -> nuevosCriterios.add(new CriterioDTO("ORIGEN", o)));

            if (categorias != null) {
                categorias.stream()
                        // FILTRO DE SEGURIDAD: Ignorar vacíos o nulos
                        .filter(c -> c != null && !c.trim().isEmpty())
                        .forEach(c -> nuevosCriterios.add(new CriterioDTO("CATEGORIA", c)));
            }

            if (titulos != null) {
                titulos.stream()
                        .filter(t -> t != null && !t.trim().isEmpty())
                        .forEach(t -> nuevosCriterios.add(new CriterioDTO("TITULO", t)));
            }


// Criterios de Fecha de Acontecimiento
            if (faDesde != null) {
                for (int i = 0; i < faDesde.size(); i++) {
                    CriterioDTO dto = new CriterioDTO();
                    dto.setTipoCriterio("FECHA_ACONTECIMIENTO");

                    // CONVERSIÓN EXPLICITA A STRING
                    if (faDesde.get(i) != null) {
                        dto.setFechaDesde(faDesde.get(i).toString()); // "yyyy-MM-dd"
                    }
                    if (faHasta != null && i < faHasta.size() && faHasta.get(i) != null) {
                        dto.setFechaHasta(faHasta.get(i).toString());
                    }

                    nuevosCriterios.add(dto);
                }
            }

            // Criterios de Fecha de Carga
            if (fcDesde != null) {
                for (int i = 0; i < fcDesde.size(); i++) {
                    CriterioDTO dto = new CriterioDTO();
                    dto.setTipoCriterio("FECHA_CARGA");

                    if (fcDesde.get(i) != null) {
                        dto.setFechaDesde(fcDesde.get(i).toString());
                    }

                    if (fcHasta != null && i < fcHasta.size() && fcHasta.get(i) != null) {
                        dto.setFechaHasta(fcHasta.get(i).toString());
                    }
                    nuevosCriterios.add(dto);
                }
            }


            if (lLat != null) {
                for (int i = 0; i < lLat.size(); i++) {
                    CriterioDTO dto = new CriterioDTO();
                    dto.setTipoCriterio("LUGAR");
                    dto.setLatitud(lLat.get(i));
                    if (lLon != null && i < lLon.size()) dto.setLongitud(lLon.get(i));
                    if (lRango != null && i < lRango.size()) dto.setRango(lRango.get(i));
                    if (lProvincia != null && i < lProvincia.size()) {
                        dto.setProvincia(lProvincia.get(i));
                    }
                    nuevosCriterios.add(dto);
                }
            }

            coleccion.setNuevosCriterios(nuevosCriterios);

            ColeccionDTO creada = metaMapaApiService.crearColeccion(coleccion);

            redirect.addFlashAttribute("mensaje", "Colección creada exitosamente: " + creada.getTitulo());
            return "redirect:/colecciones";

        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Error al crear la colección: " + e.getMessage());

            return "redirect:/colecciones/nueva";
        }
    }


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
