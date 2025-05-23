package ar.utn.ba.ddsi.models.entities.fuentes;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class FuenteEstatica{
  @Getter @Setter private List<String> rutas;

  public FuenteEstatica() {
    this.rutas = new ArrayList<>();
  }
}
