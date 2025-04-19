package domain.fuentes;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import domain.Hecho;
import domain.enumerados.Origen;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;



public class FuenteEstatica extends Fuente {
  @Getter private String ruta;
  @Getter public Set<Hecho> hechosCargados; //historial

  public FuenteEstatica(String ruta) {
    this.ruta = ruta;
    this.hechosCargados = new HashSet<>();
  }

  public void cargarHechos(Hecho ... nuevosHechos) {
    Collections.addAll(hechosCargados, nuevosHechos);
    System.out.println("cargue los hechos");
  }

/*
  @Override
  public Set<Hecho> leerHechos() {
    return hechosCargados;
  }*/

  @Override
  public Set<Hecho> leerHechos() {
    Set<Hecho> hechosACargar = new HashSet<>();

    try (CSVReader reader = new CSVReader(new FileReader(this.ruta))) {
      String[] fila;
      reader.readNext(); //lee la primer fila y la saltea

      while ((fila = reader.readNext()) != null) {

        String titulo = fila[0];
        String descripcion = fila[1];
        String categoria = fila[2];
        Integer latitud = Integer.parseInt(fila[3]);
        Integer longitud = Integer.parseInt(fila[4]);
        LocalDate fechaAcontecimiento = LocalDate.parse(fila[5]);
        //TODO unificar tipo de dato con los CSV que nos dieron

        Hecho hechoACargar = new Hecho(
            titulo, descripcion, categoria, latitud, longitud, fechaAcontecimiento,
            Origen.PROVENIENTE_DE_DATASET
        );

        //Un hecho está repetido si el “título” es el mismo. Se pisarán los atributos del existente
        this.hechosCargados.removeIf(hecho -> hecho.getTitulo().equals(hechoACargar.getTitulo()));
        this.hechosCargados.add(hechoACargar); //lo agrega al historial
        hechosACargar.add(hechoACargar);

      }

    } catch (IOException | CsvValidationException e) {
      throw new RuntimeException("No se pudo leer el archivo CSV");
    }
    return hechosACargar; //devuelve solo los que se cargaron en esta ejecucion del metodo
  }
}
