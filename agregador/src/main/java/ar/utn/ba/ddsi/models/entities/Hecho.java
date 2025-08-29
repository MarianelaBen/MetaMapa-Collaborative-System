package ar.utn.ba.ddsi.models.entities;

import java.time.LocalDate;
import java.util.*;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo", nullable = false)
   private String titulo;

  @Column(name = "descripcion", nullable = false)
   private String descripcion;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "categoria_id", nullable = false)
   private Categoria categoria;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ubicacion_id", nullable = false)
   private Ubicacion ubicacion;

  @Column(name = "fecha_acontecimiento", nullable = false)
   private LocalDate fechaAcontecimiento;

  @Column(name = "fecha_carga", nullable = false)
   private LocalDate fechaCarga;

  @Enumerated(EnumType.STRING)
  @Column(name = "origen")
   private Origen origen;

  @Column(name = "fue_eliminado")
   private boolean fueEliminado;

  @OneToMany
  @JoinColumn(name = "hecho_id")
   private Set<Etiqueta> etiquetas;

   @Column(name = "fuente_externa", nullable = true)
   private String fuenteExterna;

   private Map<TipoAlgoritmoDeConsenso, Boolean> consensoPorAlgoritmo;

  @ManyToOne
  @JoinColumn(name = "contribuyente_id")
   private Contribuyente contribuyente;

  @ElementCollection
  @CollectionTable(name = "path_multimedia", joinColumns = @JoinColumn(name = "hecho_id"))
  @Column(name = "path_multimedia")
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

  public void setConsensoParaAlgoritmo(TipoAlgoritmoDeConsenso algoritmo, boolean consensuado) {
    this.consensoPorAlgoritmo.put(algoritmo, consensuado);
  }

  public void limpiarConsensos() {
    this.consensoPorAlgoritmo.clear();
  }
}