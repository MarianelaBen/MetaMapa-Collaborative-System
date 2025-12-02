package ar.utn.ba.ddsi.models.entities.criterios;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("lugar")
@NoArgsConstructor
@Getter @Setter
public class CriterioLugar extends Criterio {

    @Column(name = "ubicacion", nullable = true)
    private Ubicacion ubicacion;

    @Column(name = "rango_maximo", nullable = true)
    private int rangoMaximo;

    @Column(name = "provincia_buscada", nullable = true)
    private String provinciaBuscada;

    public CriterioLugar(Ubicacion ubicacion, int rangoMaximo, String provinciaBuscada) {
        this.ubicacion = ubicacion;
        this.rangoMaximo = rangoMaximo;
        this.provinciaBuscada = provinciaBuscada;
    }

    @Override
    public boolean cumpleCriterio(Hecho hecho) {
        if (hecho.getUbicacion() == null) return false;

        boolean cumpleProvincia = true;
        boolean cumpleRadio = true;


        if (this.provinciaBuscada != null && !this.provinciaBuscada.isBlank()) {
            String provHecho = hecho.getUbicacion().getProvincia();


            if (provHecho != null) {
                cumpleProvincia = this.provinciaBuscada.equalsIgnoreCase(provHecho);
            }
        }

        if (this.ubicacion != null && this.rangoMaximo > 0) {
            if (hecho.getUbicacion().getLatitud() == null || hecho.getUbicacion().getLongitud() == null) {
                return false;
            }

            double distanciaKm = calcularDistanciaHaversine(
                    this.ubicacion.getLatitud(), this.ubicacion.getLongitud(),
                    hecho.getUbicacion().getLatitud(), hecho.getUbicacion().getLongitud()
            );

            System.out.println("Distancia calculada: " + distanciaKm + " km (Máximo: " + this.rangoMaximo + ")");

            cumpleRadio = distanciaKm <= this.rangoMaximo;
        }

        return cumpleProvincia && cumpleRadio;
    }
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio Tierra KM
        double latDist = Math.toRadians(lat2 - lat1);
        double lonDist = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}