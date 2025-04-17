package domain.fuentes;

import domain.Hecho;
import domain.enumerados.Origen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;

public class FuenteEstatica extends Fuente {
  @Getter private String ruta;
  @Getter private Set<Hecho> hechosCargados;

  public FuenteEstatica(String ruta) {
    this.ruta = ruta;
    this.hechosCargados = new HashSet<>(); //se necesita el historial para luego ver si hay alguno repetido
  }

  @Override
  public Set<Hecho> leerHechos() {
    Set<Hecho> hechosACargar = new HashSet<>();

    try(BufferedReader br = new BufferedReader(new FileReader(ruta))){
      String fila;
      br.readLine(); //lee la primer fila y la saltea

      while ((fila = br.readLine()) != null){
        String[] datos = fila.split(","); //se divide la fila por comas y cada dato pasa a ser un lugar en el array datos (son STRINGS)

        String titulo = datos[0];
        String descripcion = datos[1];
        String categoria = datos[2];
        String latitud = datos[3];
        String longitud = datos[4];
        LocalDateTime fecha = LocalDateTime.parse(datos[5]); //parse convierte el string del array en LocalDateTime para guardarlo en fecha
        String lugar = "Latitud: " + latitud + ", Longitud: " + longitud;

        Hecho hechoACargar = new Hecho(
            titulo, descripcion, categoria, lugar, fecha,
            Origen.PROVENIENTE_DE_DATASET
        );

        //Se considerará que un hecho está repetido si el “título” es el mismo. De ser así, se pisarán los atributos del existente
        //TODO

        hechosCargados.add(hechoACargar);
        hechosACargar.add(hechoACargar);

      }

    } catch (FileNotFoundException e) {
      throw new RuntimeException("No se pudo leer el archivo CSV");
    } catch (IOException e) {
      //TODO
      throw new RuntimeException(e);
    }

    return hechosCargados;

  }

}
