package ar.utn.ba.ddsi.Metamapa.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contribuyentes")
@RequiredArgsConstructor
public class ContribuyentesController {

  /*@GetMapping("/nuevo")
  public String mostrarFormularioCrear(Model model) {
    model.addAttribute("alumno", new AlumnoDTO());
    model.addAttribute("titulo", "Crear Nuevo Alumno");
    return "alumnos/crear";
  }

  @PostMapping("/crear")
  public String crearAlumno(@ModelAttribute("alumno")AlumnoDTO alumnoDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
    try {
      AlumnoDTO alumnoCreado = alumnoService.crearAlumno(alumnoDTO);

      redirectAttributes.addFlashAttribute("mensaje", "Alumno creado exitosamente");
      redirectAttributes.addFlashAttribute("tipoMensaje", "success");
      return "redirect:/alumnos/" + alumnoCreado.getLegajo();
    }
    catch (DuplicateLegajoException ex) {
      bindingResult.rejectValue("legajo", "error.legajo", ex.getMessage());
      model.addAttribute("titulo", "Crear Nuevo Alumno");
      return "alumnos/crear";
    }
    catch (ValidationException e) {
      convertirValidationExceptionABindingResult(e, bindingResult);
      model.addAttribute("titulo", "Crear Nuevo Alumno");
      return "alumnos/crear";
    }
    catch (Exception e) {
      model.addAttribute("error", "Error al crear el alumno: " + e.getMessage());
      model.addAttribute("titulo", "Crear Nuevo Alumno");
      return "alumnos/crear";
    }
  }*/
}
