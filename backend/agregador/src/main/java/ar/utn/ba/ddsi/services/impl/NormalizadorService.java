package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.normalizadores.NormalizadorCategoria;
// import ar.utn.ba.ddsi.normalizadores.NormalizadorUbicacion; // Ya no lo usamos aquí, usamos GeorefService
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NormalizadorService {

    private final NormalizadorCategoria normalizadorCategoria;
    private final GeorefService georefService; // Inyectamos nuestro servicio de API

    // Lista de conectores para el formateo de texto (Title Case)
    private static final List<String> CONECTORES = List.of("de", "del", "la", "las", "el", "los", "y", "en");

    public NormalizadorService(NormalizadorCategoria normalizadorCategoria, GeorefService georefService){
        this.normalizadorCategoria = normalizadorCategoria;
        this.georefService = georefService;
    }

    public Hecho normalizar(Hecho h) {
        if (h == null) return null;

        if (h.getCategoria() != null) {
            h.setCategoria(normalizadorCategoria.normalizarCategoria(h.getCategoria()));
        }

        if (h.getUbicacion() != null) {

            Double lat = h.getUbicacion().getLatitud();
            Double lon = h.getUbicacion().getLongitud();
            String provActual = h.getUbicacion().getProvincia();

            if ((provActual == null || provActual.isBlank()) && lat != null && lon != null) {


                String provDetectada = georefService.obtenerProvincia(lat, lon);

                if (provDetectada != null) {
                    h.getUbicacion().setProvincia(provDetectada);
                }

            }

            else if (provActual != null && !provActual.isBlank()) {

                String provFormateada = formatearTexto(provActual);
                h.getUbicacion().setProvincia(provFormateada);
            }
        }

        return h;
    }


    private String formatearTexto(String texto) {
        if (texto == null || texto.isBlank()) return texto;

        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];

            if (i == 0 || !CONECTORES.contains(palabra)) {
                if (palabra.length() > 1) {
                    resultado.append(Character.toUpperCase(palabra.charAt(0)))
                            .append(palabra.substring(1));
                } else {
                    resultado.append(Character.toUpperCase(palabra.charAt(0)));
                }
            } else {

                resultado.append(palabra);
            }

            if (i < palabras.length - 1) {
                resultado.append(" ");
            }
        }
        return resultado.toString();
    }
}