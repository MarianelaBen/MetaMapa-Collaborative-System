package ar.utn.ba.ddsi.models.entities;

import java.time.LocalDate;
import java.util.*;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Hecho {
    private Long id;
   private String titulo;
   private String descripcion;
   private Categoria categoria;
   private Ubicacion ubicacion;
   private LocalDate fechaAcontecimiento;
   private LocalDate fechaCarga;
   private Origen origen;
   private boolean fueEliminado;
   private Set<Etiqueta> etiquetas;
   private String fuenteExterna;
   private Map<TipoAlgoritmoDeConsenso, Boolean> consensoPorAlgoritmo;
   private Contribuyente contribuyente;
   private List<String> pathMultimedia;

  // private String nombreAportante;
  // private String apellidoAportante;
  // private Integer edadAportante;

  public Hecho(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fechaAcontecimiento, LocalDate fechaCarga , Origen origen, String fuenteExterna){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = fechaCarga;
    this.origen = origen;
    this.fueEliminado = false;
    this.etiquetas = new HashSet<>();
    this.fuenteExterna = fuenteExterna;
    this.consensoPorAlgoritmo = new HashMap<>();
  }

  public void agregarEtiqueta(Etiqueta etiqueta) {
    this.etiquetas.add(etiqueta);
  }

  public boolean esIgualContenido(Hecho otroHecho) {
    if (otroHecho == null){
      return false;
    }

    return Objects.equals(this.titulo, otroHecho.titulo) &&
        Objects.equals(this.descripcion, otroHecho.descripcion) &&
        Objects.equals(this.categoria.getNombre(), otroHecho.categoria.getNombre()) &&
        Objects.equals(this.ubicacion.getLatitud(), otroHecho.ubicacion.getLatitud()) &&
        Objects.equals(this.ubicacion.getLongitud(), otroHecho.ubicacion.getLongitud()) &&
        Objects.equals(this.fechaAcontecimiento, otroHecho.fechaAcontecimiento);
  }

  public boolean esConsensuado(TipoAlgoritmoDeConsenso algoritmo) { //esta basico dsp hay que agregar q si no hay algoritmo sea true o algo asi
    return this.consensoPorAlgoritmo.getOrDefault(algoritmo, true);
  }

  public void setConsensoParaAlgoritmo(TipoAlgoritmoDeConsenso algoritmo, boolean consensuado){
    this.consensoPorAlgoritmo.put(algoritmo, consensuado);
  }

  public void limpiarConsensos() {
    this.consensoPorAlgoritmo.clear();
  }
}