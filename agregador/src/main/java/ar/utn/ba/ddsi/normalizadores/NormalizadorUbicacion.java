package ar.utn.ba.ddsi.models.entities;

import java.text.Normalizer; //sirve para sacar tildes
import java.util.Map;

public class NormalizadorUbicacion {

  private Map<String,String> provinciaPorSinonimo = Map.ofEntries( //diccionario de sinonimos
      Map.entry("caba", "ciudad autonoma de buenos aires"),
      Map.entry("capital federal", "ciudad autonoma de buenos aires"),
      Map.entry("bs as", "buenos aires"),
      Map.entry("mdza", "mendoza")
  );

  public String normalizarProvincia(String provincia) {
    if(provincia == null) {
      return null;
    }

    String p = Normalizer.normalize(provincia.trim().toLowerCase(), Normalizer.Form.NFD) //NFD separa la base de la palabra de sus tildes
        .replaceAll("\\p{M}+", ""); //regex que saca las tildes y las remplaza por ""

    p = p.replaceAll("\\s+"," "); //junta varios espacios del medio si es que hay y los remplaza por un solo espacio
    return provinciaPorSinonimo.getOrDefault(p, p); //si esta en el diccionario devuelve el sinonimo, si no devuelve el mismo (ya vino normalizado)
  }

}

/* cuando creemos hechos tenemos que poner
  Ubicacion u = new Ubicacion();
  u.setProvincia(NormalizadorUbicacion.normalizarProvincia(dto.getProvincia()));
 */