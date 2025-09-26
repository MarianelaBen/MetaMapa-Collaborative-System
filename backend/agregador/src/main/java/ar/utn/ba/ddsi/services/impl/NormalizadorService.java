package ar.utn.ba.ddsi.services.impl;


import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.normalizadores.NormalizadorCategoria;
import ar.utn.ba.ddsi.normalizadores.NormalizadorUbicacion;
import org.springframework.stereotype.Service;

//Normaliza in-place categoría y ubicación
@Service
public class NormalizadorService {
  private final NormalizadorCategoria normalizadorCategoria;
  private final NormalizadorUbicacion normalizadorUbicacion;

  public NormalizadorService(NormalizadorCategoria normalizadorCategoria, NormalizadorUbicacion normalizadorUbicacion){
    this.normalizadorCategoria = normalizadorCategoria;
    this.normalizadorUbicacion = normalizadorUbicacion;
  }
  public Hecho normalizar(Hecho h) {
    if (h == null) return null;

    if (h.getCategoria() != null) {
      h.setCategoria(normalizadorCategoria.normalizarCategoria(h.getCategoria()));
    }

    if (h.getUbicacion() != null) {
      normalizadorUbicacion.normalizarUbicacion(h.getUbicacion());
    }
    return h;
  }

}
