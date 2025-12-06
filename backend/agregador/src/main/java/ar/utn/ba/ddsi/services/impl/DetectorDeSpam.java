package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DetectorDeSpam implements IDetectorDeSpam {

    // Detecta 4 o más caracteres idénticos consecutivos (ej: "aaaaa", "holaaaaa")
    private static final Pattern CARACTERES_REPETIDOS = Pattern.compile("(.)\\1{4,}");

    public boolean esSpam(String texto) {
        if (texto == null) return true;

        if (texto.trim().length() < 500) {
            return true;
        }

        if (CARACTERES_REPETIDOS.matcher(texto).find()) {
            return true;
        }

        String[] palabras = texto.toLowerCase().split("\\s+");
        Set<String> palabrasUnicas = Arrays.stream(palabras).collect(Collectors.toSet());

        double ratioDiversidad = (double) palabrasUnicas.size() / palabras.length;
        if (palabras.length > 10 && ratioDiversidad < 0.10) {
            return true;
        }

        String textoLower = texto.toLowerCase();
        if (textoLower.contains("spam_test") ||
                textoLower.contains("casino online") ||
                textoLower.contains("viagra")) {
            return true;
        }

        return false;
    }
}
