/*package ar.utn.ba.ddsi.Metamapa.models.entities;

import ar.utn.ba.ddsi.Metamapa.enums.Permiso;
import ar.utn.ba.ddsi.Metamapa.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    @Column(unique = true) private String nombreDeUsuario;
    private String contrasenia; // cifrada
    @Enumerated(EnumType.STRING) private Rol rol;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Permiso> permisos = new HashSet<>();

    public void agregarPermiso(Permiso p) {
        this.permisos.add(p);
    }
}*/
