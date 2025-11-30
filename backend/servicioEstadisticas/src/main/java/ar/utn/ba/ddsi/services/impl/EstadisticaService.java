package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.services.IEstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticaService implements IEstadisticaService {

    private final WebClient webClient;
    private final IEstadisticaRepository estadisticaRepository;

    // URLs directas para evitar problemas de URI Builder
    private String agregadorUrl = "http://localhost:8083/api/public";
    private String adminUrl     = "http://localhost:8083/api/admin";

    @Autowired
    public EstadisticaService(IEstadisticaRepository estadisticaRepository,
                              WebClient.Builder webClientBuilder) {
        this.estadisticaRepository = estadisticaRepository;
        // Aumentamos memoria para traer muchos hechos
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        this.webClient = webClientBuilder.exchangeStrategies(strategies).build();
    }

    @Override
    public void recalcularEstadisticas() {
        // 1. IMPORTANTE: Traemos DTOs y convertimos a Entidades para que tengan los datos bien mapeados
        List<Hecho> todosLosHechos = this.obtenerTodosLosHechos();
        List<Coleccion> colecciones = this.obtenerColecciones();
        List<Categoria> categorias = this.obtenerCategorias();
        LocalDateTime ahora = LocalDateTime.now();

        if (todosLosHechos.isEmpty()) {
            System.out.println("⚠️ No hay hechos para calcular estadísticas.");
            return;
        }

        // 2. Cálculo: Provincia con mas hechos por coleccion
        colecciones.forEach(col -> {
            // Optimizacion: Filtramos en memoria de la lista global en vez de llamar N veces al agregador
            // (Opcional, si prefieres llamar al endpoint específico descomenta obtenerHechosDeColeccion)
            // Por simplicidad y coherencia de datos usamos la lista global convertida:
            List<Hecho> hechosColeccion = obtenerHechosDeColeccion(col.getHandle())
                    .stream()
                    .map(this::convertirAEntidad) // Mapeo necesario
                    .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
                    .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
                    .toList();

            if (hechosColeccion.isEmpty()) return;

            Map<String, Long> conteo = hechosColeccion.stream()
                    .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()));

            Map.Entry<String, Long> top = conteo.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

            if (top == null) return;

            HechosPorProvinciaEnColeccion sub = new HechosPorProvinciaEnColeccion();
            sub.setColeccionHandle(col.getHandle());
            sub.setCantidadPorProvincia(conteo);
            sub.setProvinciaGanadora(top.getKey());
            sub.setCantidadGanadora(top.getValue());

            Estadistica snap = new Estadistica();
            snap.setFechaDeCalculo(ahora);
            snap.setHechosPorProvinciaEnColeccion(sub);

            estadisticaRepository.save(snap);
        });

        // 3. Categoria Top Global
        {
            List<Hecho> activos = todosLosHechos.stream()
                    .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
                    .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
                    .toList();

            if (!activos.isEmpty()) {
                Map<String, Long> conteo = activos.stream()
                        .collect(Collectors.groupingBy(h -> h.getCategoria().getNombre(), Collectors.counting()));

                Map.Entry<String, Long> top = conteo.entrySet().stream()
                        .max(Map.Entry.comparingByValue()).orElse(null);

                if (top != null) {
                    CategoriaTopGlobal sub = new CategoriaTopGlobal();
                    sub.setCantidadPorCategoria(conteo);
                    sub.setCategoriaGanadora(top.getKey());
                    sub.setCantidadGanadora(top.getValue());

                    Estadistica snap = new Estadistica();
                    snap.setFechaDeCalculo(ahora);
                    snap.setCategoriaTopGlobal(sub); // Asegúrate que tu entidad use este nombre

                    estadisticaRepository.save(snap);
                }
            }
        }

        // 4. Provincia Top por Categoria
        categorias.forEach(cat -> {
            String nombreCat = cat.getNombre();
            if (nombreCat == null) return;

            List<Hecho> hechosCat = todosLosHechos.stream()
                    .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
                    .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
                    .filter(h -> h.getCategoria().getNombre().equalsIgnoreCase(nombreCat))
                    .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
                    .toList();

            if (hechosCat.isEmpty()) return;

            Map<String, Long> conteo = hechosCat.stream()
                    .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()));

            Map.Entry<String, Long> top = conteo.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);

            if (top == null) return;

            ProvinciaTopPorCategoria sub = new ProvinciaTopPorCategoria();
            sub.setCategoria(nombreCat);
            sub.setCantidadPorProvincia(conteo);
            sub.setProvinciaGanadora(top.getKey());
            sub.setCantidadGanadora(top.getValue());

            Estadistica snap = new Estadistica();
            snap.setFechaDeCalculo(ahora);
            snap.setProvinciaTopPorCategoria(sub);

            estadisticaRepository.save(snap);
        });

        // 5. Horario Pico
        categorias.forEach(cat -> {
            String nombreCat = cat.getNombre();
            if (nombreCat == null) return;

            List<Hecho> hechosCat = todosLosHechos.stream()
                    .filter(h -> Boolean.FALSE.equals(h.getFueEliminado()))
                    .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
                    .filter(h -> h.getCategoria().getNombre().equalsIgnoreCase(nombreCat))
                    .filter(h -> h.getFechaYHoraAcontecimiento() != null)
                    .toList();

            if (hechosCat.isEmpty()) return;

            Map<Integer, Long> conteo = hechosCat.stream()
                    .collect(Collectors.groupingBy(
                            h -> h.getFechaYHoraAcontecimiento().getHour(), // 0..23
                            Collectors.counting()
                    ));

            Map.Entry<Integer, Long> top = conteo.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);

            if (top == null) return;

            HorarioPicoPorCategoria sub = new HorarioPicoPorCategoria();
            sub.setCategoria(nombreCat);
            sub.setCantidadPorHora(conteo);
            sub.setHoraGanadora(top.getKey());
            sub.setCantidadGanadora(top.getValue());

            Estadistica snap = new Estadistica();
            snap.setFechaDeCalculo(ahora);
            snap.setHorarioPicoPorCategoria(sub);

            estadisticaRepository.save(snap);
        });

        // 6. Solicitudes Spam
        {
            List<SolicitudDeEliminacion> solicitudes = this.obtenerSolicitudes();
            long total = solicitudes.size();
            long spam  = solicitudes.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getEsSpam()))
                    .count();

            SolicitudesEliminacionSpam sub = new SolicitudesEliminacionSpam();
            sub.setTotal(total);
            sub.setSpam(spam);

            Estadistica snap = new Estadistica();
            snap.setFechaDeCalculo(ahora);
            snap.setSolicitudesEliminacionSpam(sub); // Asegúrate que tu entidad use este nombre

            estadisticaRepository.save(snap);
        }
    }


    public List<Hecho> obtenerTodosLosHechos() {
        try {
            List<HechoOutputDTO> dtos = webClient.get()
                    .uri(agregadorUrl + "/hechos")
                    .retrieve()
                    .bodyToFlux(HechoOutputDTO.class)
                    .collectList()
                    .block();

            if (dtos == null) return List.of();

            // Convertimos a Entidad Local
            return dtos.stream().map(this::convertirAEntidad).collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            System.err.println("Error obteniendo hechos: " + e.getMessage());
            return List.of();
        }
    }

    // Conversor DTO -> Entidad
    private Hecho convertirAEntidad(HechoOutputDTO dto) {
        Hecho h = new Hecho();
        h.setTitulo(dto.getTitulo());
        h.setDescripcion(dto.getDescripcion());
        h.setFechaYHoraAcontecimiento(dto.getFechaAcontecimiento());
        h.setFueEliminado(dto.getFueEliminado());

        if (dto.getCategoria() != null) {
            Categoria c = new Categoria();
            c.setNombre(dto.getCategoria());
            h.setCategoria(c);
        }

        if (dto.getProvincia() != null) {
            Ubicacion u = new Ubicacion();
            u.setProvincia(dto.getProvincia());
            u.setLatitud(dto.getLatitud());
            u.setLongitud(dto.getLongitud());
            h.setUbicacion(u);
        }

        return h;
    }

    public List<HechoOutputDTO> obtenerHechosDeColeccion(String handle) {
        try {
            return webClient.get()
                    .uri(agregadorUrl + "/colecciones/" + handle + "/hechos")
                    .retrieve()
                    .bodyToFlux(HechoOutputDTO.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        }
    }

    public List<Coleccion> obtenerColecciones() {
        try {
            return webClient.get()
                    .uri(adminUrl + "/colecciones")
                    .retrieve()
                    .bodyToFlux(Coleccion.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            return List.of();
        }
    }

    public List<Categoria> obtenerCategorias() {
        try {
            return webClient.get()
                    .uri(agregadorUrl + "/categorias")
                    .retrieve()
                    .bodyToFlux(Categoria.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        }
    }

    public List<SolicitudDeEliminacion> obtenerSolicitudes() {
        try {
            return webClient.get()
                    .uri(agregadorUrl + "/solicitudes")
                    .retrieve()
                    .bodyToFlux(SolicitudDeEliminacion.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        }
    }
}