package ar.utn.ba.ddsi.Metamapa.controllers;

import ar.utn.ba.ddsi.Metamapa.exceptions.ValidationException;
import ar.utn.ba.ddsi.Metamapa.models.dtos.CategoriaDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.Metamapa.exceptions.NotFoundException;
import ar.utn.ba.ddsi.Metamapa.services.ColeccionService;
import ar.utn.ba.ddsi.Metamapa.services.HechoService;
import ar.utn.ba.ddsi.Metamapa.services.MetaMapaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hechos")
@RequiredArgsConstructor
public class HechoController {
  @Value("${backend.origin}")
  private String backendOrigin;
  @Value("${backend.dinamica}")
  private String backendDinamica;
  private final HechoService hechoService;
  private final MetaMapaApiService metaMapaApiService;
  private final ColeccionService coleccionService;

// En HechoController.java

    @GetMapping("/{id}")
    public String verDetalleHecho(
            @PathVariable Long id,
            @RequestParam(required = false) String handle, // <--- NUEVO PARÁMETRO
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            HechoDTO hecho;

            if (handle != null && !handle.isBlank()) {
                hecho = hechoService.getHechoDeColeccion(handle, id);

                model.addAttribute("coleccionHandle", handle);
                model.addAttribute("coleccionTitulo", "Volver a la colección");
            } else {
                hecho = hechoService.getHechoPorId(id);
            }

            List<String> nombresMultimedia = (hecho.getIdContenidoMultimedia() == null)
                    ? List.of()
                    : hecho.getIdContenidoMultimedia();

            model.addAttribute("hecho", hecho);
            model.addAttribute("nombresMultimedia", nombresMultimedia);
            model.addAttribute("extensionesImagen", List.of("jpg", "jpeg", "png", "gif", "webp"));
            model.addAttribute("backendOrigin", backendOrigin);
            model.addAttribute("titulo", "Hecho " + hecho.getTitulo());

            return "hechosYColecciones/detalleHecho";
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
            return "redirect:/404";
        }
    }

  @GetMapping("/nuevo")
  public String verFormulario(Model model) {
    List<CategoriaDTO> categorias = this.coleccionService.getCategorias();
    model.addAttribute("titulo", "Subir hecho");
    model.addAttribute("hecho", new HechoDTO(
        null,
        null,
        null,
        null,
        null, null
    ));
    model.addAttribute("categorias", categorias);

    return "hechosYColecciones/formularioHecho";
  }

  @PostMapping("/nuevo")
  public String procesarFormulario(@ModelAttribute("hecho") HechoDTO hecho,
                                   @RequestParam("fecha") String fecha,
                                   @RequestParam("hora") String hora,
                                   @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia,
                                   RedirectAttributes redirect, Model model) {

    //si algun campo obligatorio no esta, que tire error
    boolean error = false;
    if (hecho.getTitulo() == null || hecho.getTitulo().isBlank()) {
      error = true;
    }
    if (hecho.getCategoria() == null || hecho.getCategoria().isBlank()) {
      error = true;
    }
    if (hecho.getDescripcion() == null || hecho.getDescripcion().isBlank()) {
      error = true;
    }
    if (hecho.getProvincia() == null || hecho.getProvincia().isBlank()) {
      error = true;
    }
    if (hecho.getLatitud() == null || hecho.getLongitud() == null) {
      error = true;
    }
    if (fecha == null || fecha.isBlank() || hora == null || hora.isBlank()) {
      error = true;
    }

    try {
      // convierto la hora y fecha que vienen separados desde el formulario, a LocalDateTime para el DTO
      if (!error) {
        LocalDate d = LocalDate.parse(fecha);
        LocalTime t = LocalTime.parse(hora);
        hecho.setFechaAcontecimiento(LocalDateTime.of(d, t));
      }
    } catch (Exception e) {
      error = true;
    }

    if (error) { //esto es si ya se mando el formulario y por alguna razon llega un campo obligatorio vacio
      // si hay error que vuelva a cargar el form y le pase al model los mismos datos harcodeados
      model.addAttribute("titulo", "Subir hecho");
      model.addAttribute("categorias", List.of("Incendio forestal", "Accidente vial", "Inundación"));
      model.addAttribute("localidades", List.of("CABA", "La Plata", "Rosario"));
      model.addAttribute("error", "Competar los campos obligatorios.");
      return "hechosYColecciones/formularioHecho";
    }

    if (multimedia != null) { //esto es un log para ver si se estan subiendo, por que en la pantalla no aparece
      for (MultipartFile file : multimedia) {
        if (file != null && !file.isEmpty()) {
          System.out.println("Llego el archivo: " + file.getOriginalFilename()); //si subiste un archivo, por consola debe aparecer el nombre
        }
      }
    }

    Long usuarioId = metaMapaApiService.getUsuarioIdFromAccessToken();
    HechoDTO creado = this.hechoService.subirHecho(hecho, multimedia, usuarioId);
    redirect.addFlashAttribute("mensaje",
        "Tu hecho ha sido creado correctamente. Podrás verlo en la próxima actualización del mapa.");
    redirect.addFlashAttribute("tipoMensaje", "success");

    return "redirect:/hechos/nuevo";

  }

  @GetMapping("/{id}/editar")
  // @PreAuthorize("hasAnyRole('CONTRIBUYENTE') and hasAnyAuthority('EDITAR_HECHO_PROPIO')")
  @PreAuthorize("permitAll()") // ← TEMPORAL para probar
  public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {

      HechoDTO hecho = this.metaMapaApiService.getHechoPorId(id);
      List<CategoriaDTO> categorias = this.metaMapaApiService.getCategorias();

      List<String> nombresMultimedia =
          hecho.getIdContenidoMultimedia() == null ? List.of()
              : hecho.getIdContenidoMultimedia().stream()
              .map(p -> {
                if (p == null) return null;

                if (p.startsWith("http://") || p.startsWith("https://")) {
                  return p;
                }

                if (p.contains("\\") || p.contains(":")) {
                  String fileName = p.substring(p.replace("\\","/").lastIndexOf("/") + 1);
                  return "/uploads/" + fileName;
                }

                if (p.startsWith("/uploads/")) return p;

                return "/uploads/" + p;
              })
              .toList();

      System.out.println("ID DE FUENTE RECIBIDO EN EL FORM: " + hecho.getIdEnFuente());
      model.addAttribute("nombresMultimedia", nombresMultimedia);
      model.addAttribute("hecho", hecho);
      model.addAttribute("categorias", categorias);
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      model.addAttribute("mediaBaseUrl", "/uploads");
      model.addAttribute("extensionesImagen", List.of("jpg", "jpeg", "png", "gif", "webp"));
      model.addAttribute("backendOrigin", backendOrigin);
      return "contribuyente/editorHechos";

    } catch (NotFoundException ex) {
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      return "redirect:/404";
    }
  }

  /*@PostMapping("/{id}/editar")
  @PreAuthorize("hasAnyRole('CONTRIBUYENTE') and hasAnyAuthority('EDITAR_HECHO_PROPIO')")
  public String procesarEdicion(@PathVariable Long id,
                                @ModelAttribute("hecho") HechoDTO hecho,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia,
                                @RequestParam(name = "replaceMedia", defaultValue = "false") boolean replaceMedia,
                                @RequestParam(value = "deleteExisting", required = false) List<String> deleteExisting,
                                RedirectAttributes redirect,
                                Model model) {

    boolean error = false;

    if (hecho.getTitulo() == null || hecho.getTitulo().isBlank()) {
      error = true;
    }
    if (hecho.getCategoria() == null || hecho.getCategoria().isBlank()) {
      error = true;
    }
    if (hecho.getDescripcion() == null || hecho.getDescripcion().isBlank()) {
      error = true;
    }
    if (hecho.getProvincia() == null || hecho.getProvincia().isBlank()) {
      error = true;
    }
    if (fecha == null || fecha.isBlank() || hora == null || hora.isBlank()) {
      error = true;
    }

    try {
      if (!error) {
        LocalDate d = LocalDate.parse(fecha);
        LocalTime t = LocalTime.parse(hora);
        hecho.setFechaAcontecimiento(LocalDateTime.of(d, t));
      }
    } catch (Exception e) {
      error = true;
    }

    if (error) {
      model.addAttribute("categorias", hechoService.getCategorias());
      model.addAttribute("hechoId", id);
      model.addAttribute("titulo", "Editar Hecho");
      model.addAttribute("errorMsg", "Hay campos obligatorios sin completar.");
      return "contribuyente/editorHechos";
    }

    hecho.setId(id);
    hechoService.actualizarHecho(id, hecho, multimedia, replaceMedia,
        deleteExisting == null ? List.of() : deleteExisting);

    redirect.addFlashAttribute("mensaje", "Hecho actualizado correctamente");
    redirect.addFlashAttribute("tipoMensaje", "success");
    return "redirect:/hechos/" + id;
  }*/ //TODO borrar cuando la nueva edicion funcione

  @PostMapping("/{id}/editar")
  @PreAuthorize("hasAnyRole('CONTRIBUYENTE')") //TODO borre esta parte  and hasAnyAuthority('EDITAR_HECHO_PROPIO') porque no se no sirve por ahora
  public String procesarEdicion(
      @PathVariable Long id, @ModelAttribute("hecho") HechoDTO hecho, @RequestParam("fecha") String fecha, @RequestParam("hora") String hora,
      @RequestParam(name = "multimedia", required = false) MultipartFile[] multimedia, @RequestParam(name = "replaceMedia", defaultValue = "false") boolean replaceMedia,
      @RequestParam(value = "deleteExisting", required = false) List<String> deleteExisting,
      RedirectAttributes redirect, Model model) {
    hecho.setId(id);
    Long usuarioId = metaMapaApiService.getUsuarioIdFromAccessToken();

    if (fecha != null && !fecha.isBlank() && hora != null && !hora.isBlank()) {
      try {
        LocalDate d = LocalDate.parse(fecha);
        LocalTime t = LocalTime.parse(hora);
        hecho.setFechaAcontecimiento(LocalDateTime.of(d, t));
      } catch (Exception e) {
        redirect.addFlashAttribute("mensaje", "La fecha u hora ingresada no es válida.");
        redirect.addFlashAttribute("tipoMensaje", "danger");
        return "redirect:/hechos/" + id;
      }
    }

    try {
      hechoService.actualizarHecho(
          hecho.getIdEnFuente(),
          hecho,
          multimedia,
          replaceMedia,
          deleteExisting == null ? List.of() : deleteExisting,
          usuarioId
      );
    } catch (Exception e) {
      redirect.addFlashAttribute("mensaje", "Ocurrió un error enviando la edición: " + e.getMessage());
      redirect.addFlashAttribute("tipoMensaje", "danger");
      return "redirect:/hechos/" + id;
    }

    redirect.addFlashAttribute("mensaje", "Su hecho fue editado con éxito. "
        + "Los cambios serán visibles una vez aprobados.");
    redirect.addFlashAttribute("tipoMensaje", "success");

    return "redirect:/hechos/" + id;
  }


  // metodo usado en editorHechos (para el contenido multimedia)
  private static String filenameFromPath(String p) {
    if (p == null) return "";
    int slash = p.lastIndexOf('/');
    int back = p.lastIndexOf('\\');
    int idx = Math.max(slash, back);
    return idx >= 0 ? p.substring(idx + 1) : p;
  }

  @PostMapping("/{id}/sumarVista")
  public String sumarVistaHecho(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
    try {
      hechoService.sumarVistaHecho(id);
      redirectAttributes.addFlashAttribute("mensaje", "Se sumó una vista al hecho exitosamente");
      redirectAttributes.addFlashAttribute("tipoMensaje", "success");
      return "redirect:/detalleHecho/" + id;
    } catch (NotFoundException ex) {
      redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
      redirectAttributes.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/detalleHecho/" + id;
    } catch (ValidationException e) {
      redirectAttributes.addFlashAttribute("mensaje", "Error de validación: " + e.getMessage());
      redirectAttributes.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/detalleHecho/" + id;
    } catch (Exception e) {

      redirectAttributes.addFlashAttribute("mensaje", "Error al sumar vista al hecho");
      redirectAttributes.addFlashAttribute("tipoMensaje", "error");
      return "redirect:/detalleHecho/" + id;
    }
  }

  @GetMapping("/mis-hechos")
  public String verMisHechos(
      @RequestParam(required = false) String titulo,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) String estado,
      Model model) {

    Long usuarioId = metaMapaApiService.getUsuarioIdFromAccessToken();

    List<HechoDTO> hechos = hechoService.getMisHechosFiltrado(
        usuarioId, titulo, categoria, estado);

    List<CategoriaDTO> categorias = coleccionService.getCategorias();

    model.addAttribute("hechos", hechos);
    model.addAttribute("categorias", categorias);
    model.addAttribute("titulo", "Mis Hechos");
    model.addAttribute("descripcion",
        "Gestiona los hechos que has reportado. (uid token: " + usuarioId + ")");

    Map<String, String> param = new HashMap<>();
    param.put("titulo", titulo);
    param.put("categoria", categoria);
    param.put("estado", estado);

    model.addAttribute("param", param);

    return "contribuyente/misHechos";
  }

}