package ar.utn.ba.ddsi.controllers.impl;


import ar.utn.ba.ddsi.controllers.IEstadisticaController;
import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.services.IEstadisticaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController implements IEstadisticaController {
  private final IEstadisticaService estadisticasService;
  private final IEstadisticaRepository estadisticaRepository;

  @Autowired
  public EstadisticaController(IEstadisticaService estadisticasService,
                               IEstadisticaRepository estadisticaRepository) {
    this.estadisticasService = estadisticasService;
    this.estadisticaRepository = estadisticaRepository;
  }

  @GetMapping
  public ResponseEntity<List<Estadistica>> obtenerEstadisticas() {
    return ResponseEntity.ok(estadisticaRepository.findAll());
  }


  @GetMapping("/pregunta-id/{id}")
  public ResponseEntity<List<Estadistica>> obtenerEstadisticasPorPregunta(@PathVariable Long id) {
    return ResponseEntity.ok(
        estadisticaRepository.findAllByPreguntaIdOrderByFechaDeCalculoDesc(id) //te devuelve las mas recientes primero
    );
  }

  @GetMapping("/pregunta-id/{id}/ultima")
  public ResponseEntity<Estadistica> obtenerUltimaEstadisticaPorPregunta(@PathVariable Long id) {
    return estadisticaRepository.findTopByPreguntaIdOrderByFechaDeCalculoDesc(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/preguntaid/{id}/coleccion/{handle}")
  public ResponseEntity<List<Estadistica>> obtenerEstadisticasPorPreguntaYColeccion(@PathVariable Long id,
                                                                 @PathVariable String handle) {
    return ResponseEntity.ok(
        estadisticaRepository.findAllByPreguntaIdAndColeccionHandleOrderByFechaDeCalculoDesc(id, handle)
    );
  }

  @GetMapping(value = "/export", produces = "text/csv") //produce le dice a Spring que la respuesta es CSV
  public void exportarCSV(HttpServletResponse respuesta) throws IOException { //escribe en directo en la respuesta HTTP
    respuesta.setHeader("Content-Disposition", "attachment; filename=estadisticas.csv"); //lo pone en un header para que el navegador descargue un archivo

    //Traemos los datos
    List<Estadistica> estadisticas = estadisticaRepository.findAll();

    try (PrintWriter w = respuesta.getWriter()) { //pide un writer para escribir texto en la respuesta
      w.println("id,pregunta,coleccion_handle,categoria_id,provincia,hora_del_dia,valor,fecha_de_calculo"); //encabezado del csv
      for (Estadistica e : estadisticas) {
        String pregunta = e.getPregunta() != null ? e.getPregunta().getPregunta() : ""; //si la pregunta existe usa su texto, si no cadena vacia
        w.printf("%d,%s,%s,%s,%s,%s,%d,%s%n", //una fila + 8 columnas. Al final con %n agrega salto de línea
            e.getId(),
            revisar(pregunta),
            revisar(e.getColeccionHandle()),
            e.getCategoriaId() == null ? "" : e.getCategoriaId().toString(),// si es null que quede vacio
            revisar(e.getProvincia()),
            e.getHoraDelDia() == null ? "" : e.getHoraDelDia().toString(),//si es null que quede vacio
            e.getValor(),
            e.getFechaDeCalculo()
        );
      }
    }
  }

  //este metodo sirve para que no piense que si hay una coma
  private String revisar(String s) { //por si tiene comas o comillas
    if (s == null) return "";
    boolean necesitaComillas = s.contains(",") || s.contains("\"") || s.contains("\n"); //si tiene coma, comillas o salto de linea
    return necesitaComillas ? "\"" + s.replace("\"", "\"\"") + "\"" : s; // ahi envuelve entre comillas para que no piense que son columnas separadas
  }


}

