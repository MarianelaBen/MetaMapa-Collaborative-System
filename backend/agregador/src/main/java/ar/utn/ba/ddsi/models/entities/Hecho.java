package ar.utn.ba.ddsi.models.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", nullable = false)
   private Categoria categoria;

  @Embedded
  private Ubicacion ubicacion;

  @Column(name = "fecha_acontecimiento", nullable = false)
  private LocalDateTime fechaAcontecimiento;

  @Column(name = "fecha_carga", nullable = false)
  private java.time.LocalDate fechaCarga;

  @Enumerated(EnumType.STRING)
  @Column(name = "origen")
   private Origen origen;

  @Column(name = "fue_eliminado")
  private Boolean fueEliminado;

  @ManyToMany
  @JoinTable(name = "hecho_etiqueta",
  joinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"),
  inverseJoinColumns = @JoinColumn(name = "etiqueta_id",
  referencedColumnName = "id")
  )
   private Set<Etiqueta> etiquetas = new HashSet<>(); //TODO revisar si funciona bien si lo inicializo aca

   @Column(name = "fuente_externa", nullable = true)
   private String fuenteExterna;

   @ElementCollection
   @CollectionTable(name = "hecho_consenso", joinColumns = @JoinColumn(name = "hecho_id" ))
   @MapKeyEnumerated(EnumType.STRING)
   @MapKeyColumn(name = "algoritmo")
   @Column(name = "aprobado", nullable = false)
   private Map<TipoAlgoritmoDeConsenso, Boolean> consensoPorAlgoritmo = new EnumMap<>(TipoAlgoritmoDeConsenso.class); //mismo chequeo que arriba

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contribuyente_id")
   private Contribuyente contribuyente;

  @ElementCollection
  @CollectionTable(name = "path_multimedia", joinColumns = @JoinColumn(name = "hecho_id"))
  @Column(name = "path_multimedia")
   private List<String> pathMultimedia = new ArrayList<>(); //mismo

  @Column(name = "rutaNombre", nullable = true)
     private String rutaNombre;

  @Column(name = "fechaActualizacion", nullable = true)
   private LocalDate fechaActualizacion;

  @Column(name = "cantidad_vistas")
  private Integer cantVistas;

  //@Column(name = "editable" )
  //private boolean editable;

  //@Column(name = "diasRestantesEdicion")
  //private int diasRestantesEdicion;

  // private String nombreAportante;
  // private String apellidoAportante;
  // private Integer edadAportante;

  public Hecho(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDateTime fechaAcontecimiento, LocalDate fechaCarga , Origen origen, String fuenteExterna){
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

    return Objects.equals(this.titulo.trim(), otroHecho.titulo.trim()) &&
        Objects.equals(this.descripcion.trim(), otroHecho.descripcion.trim()) &&
        Objects.equals(this.categoria.getNombre().trim().toUpperCase(), otroHecho.categoria.getNombre().trim().toUpperCase()) &&
        Objects.equals(this.ubicacion.getLatitud(), otroHecho.ubicacion.getLatitud()) &&
        Objects.equals(this.ubicacion.getLongitud(), otroHecho.ubicacion.getLongitud()) &&
        Objects.equals(this.fechaAcontecimiento.toLocalDate(), otroHecho.fechaAcontecimiento.toLocalDate());
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