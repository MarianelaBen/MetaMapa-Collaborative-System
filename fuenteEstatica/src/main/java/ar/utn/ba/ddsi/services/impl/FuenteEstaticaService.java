package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputEstaticaDTO;
import ar.utn.ba.ddsi.models.dtos.output.RutaOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.dtos.output.UbicacionOutputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.IRutasRepository;
import ar.utn.ba.ddsi.services.IFuenteEstaticaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuenteEstaticaService implements IFuenteEstaticaService {

  @Autowired
  public IHechoRepository hechoRepository;
  @Autowired
  public IRutasRepository rutasRepository;
  @Autowired
  public ICategoriaRepository categoriaRepository;

  @Override
  public void leerHechos(Long idRuta) {
    Ruta ruta = rutasRepository.findById(idRuta).orElseGet(null);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

    try (CSVReader reader = new CSVReader(new FileReader(ruta.getPath()))) {
      String[] fila;
      reader.readNext(); //lee la primer fila y la saltea

      while ((fila = reader.readNext()) != null) {
        System.out.println("La direccion esta bien");
        String titulo = fila[0];
        String descripcion = fila[1];
        Categoria categoria = new Categoria(fila[2]);
        Ubicacion ubicacion = new Ubicacion(Double.parseDouble(fila[3]), Double.parseDouble(fila[4]));
        LocalDate fechaAcontecimiento = LocalDate.parse(fila[5], formatter);

        categoriaRepository.save(categoria);

        Hecho hechoACargar = new Hecho(
            titulo, descripcion, categoria, ubicacion, fechaAcontecimiento,
            Origen.PROVENIENTE_DE_DATASET
        );

        hechoACargar.setRuta(ruta);
        this.hechoRepository.save(hechoACargar);
      }
    } catch (IOException | CsvValidationException e) {
      throw new RuntimeException("No se pudo leer el archivo CSV");
    }
  }

  @Override
  public void leerTodosLosArchivos() {
    for (Ruta ruta : rutasRepository.findAll()) {
      try {
        System.out.println(ruta.getIdRuta());
        System.out.println(ruta.getPath());
        this.leerHechos(ruta.getIdRuta());
      } catch (Exception e) {
        throw new RuntimeException("No hay archivos para leer");
      }
    }
  }

  @Override
  public List<HechoOutputEstaticaDTO> buscarTodos() {
    return this.hechoRepository
        .findAll()
        .stream()
        .map(this::hechoOutputEstaticaDTO)
        .toList();
  }

  @Override
  public HechoOutputEstaticaDTO hechoOutputEstaticaDTO(Hecho hecho) {
    HechoOutputEstaticaDTO dto = new HechoOutputEstaticaDTO();
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getCategoria().getNombre());
    dto.setUbicacion(ubicacionOutputDTO(hecho.getUbicacion()));
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode extras = mapper.createObjectNode();
    if (hecho.getRuta() != null && hecho.getRuta().getNombre() != null) {
      extras.put("rutaNombre", hecho.getRuta().getNombre());
    }
    dto.setParticulares(extras);

    dto.setEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toSet()));
    return dto;
  }

  @Override
  public UbicacionOutputDTO ubicacionOutputDTO(Ubicacion ubicacion) {
    UbicacionOutputDTO dto = new UbicacionOutputDTO();
    dto.setLatitud(ubicacion.getLatitud());
    dto.setLongitud(ubicacion.getLongitud());
    return dto;
  }

  @Override
  public RutaOutputDTO crearRuta(String nombre, String path){
    Ruta ruta = new Ruta();
    ruta.setNombre(nombre);
    ruta.setPath(path);
    rutasRepository.save(ruta);
    System.out.println(ruta.getIdRuta());
    return rutaOutputDTO(ruta);
  }

  public RutaOutputDTO rutaOutputDTO(Ruta ruta) {
    RutaOutputDTO rutaOutput = new RutaOutputDTO();
    rutaOutput.setIdRuta(ruta.getIdRuta());
    rutaOutput.setNombre(ruta.getNombre());
    rutaOutput.setPath(ruta.getPath());
    return rutaOutput;
  }
}
