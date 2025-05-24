package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ruta;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.IRutasRepository;
import ar.utn.ba.ddsi.services.IFuenteEstaticaService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FuenteEstaticaService implements IFuenteEstaticaService {

  @Autowired
  public IHechoRepository hechoRepository;
  @Autowired
  public IRutasRepository rutasRepository;

  @Override
  public void leerHechos(Long idRuta) {
    Ruta ruta = rutasRepository.findById(idRuta);
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

          Hecho hechoACargar = new Hecho(
              titulo, descripcion, categoria, ubicacion, fechaAcontecimiento,
              Origen.PROVENIENTE_DE_DATASET
          );

          this.hechoRepository.save(hechoACargar);
        }
      } catch (IOException | CsvValidationException e) {
        throw new RuntimeException("No se pudo leer el archivo CSV");
      }
    }
}
