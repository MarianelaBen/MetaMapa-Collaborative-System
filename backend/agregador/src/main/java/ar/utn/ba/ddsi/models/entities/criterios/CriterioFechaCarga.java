package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("fecha_carga")
@NoArgsConstructor
public class CriterioFechaCarga extends Criterio {

    @Column(name = "fecha_carga_desde", nullable = true)
    private LocalDate desde;

    @Column(name = "fecha_carga_hasta", nullable = true)
    private LocalDate hasta;

    public CriterioFechaCarga(LocalDate desde, LocalDate hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public boolean cumpleCriterio(Hecho hecho) {
        if (hecho.getFechaCarga() == null) return false;

        return !hecho.getFechaCarga().isBefore(desde) && !hecho.getFechaCarga().isAfter(hasta);
    }
}