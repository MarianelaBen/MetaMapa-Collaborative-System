package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.enums.Permiso;
import ar.utn.ba.ddsi.models.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String nombreDeUsuario;
    private String contrasenia; // cifrada
    private String mail;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Permiso> permisos = new ArrayList<>();

    public void agregarPermiso(Permiso p) {
        this.permisos.add(p);
    }
}