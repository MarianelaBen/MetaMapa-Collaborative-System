package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import ar.utn.ba.ddsi.services.IEstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String agregadorUrl = "https://agregador-production-0577.up.railway.app/api/public";

    @Autowired
    public EstadisticaService(IEstadisticaRepository estadisticaRepository,
                              WebClient.Builder webClientBuilder) {
        this.estadisticaRepository = estadisticaRepository;
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        this.webClient = webClientBuilder.exchangeStrategies(strategies).build();
    }

    @Override
    public void recalcularEstadisticas() {
        System.out.println(">>> INICIANDO CÁLCULO ESTADÍSTICAS (VERSIÓN UNIFICADA)...");
        LocalDateTime ahora = LocalDateTime.now();

        List<Hecho> todosLosHechos = this.obtenerTodosLosHechos();
        List<SolicitudDeEliminacion> solicitudes = this.obtenerSolicitudes();

        if (todosLosHechos.isEmpty()) return;

        // 1. Crear UN SOLO objeto Snapshot
        Estadistica snapshot = new Estadistica();
        snapshot.setFechaDeCalculo(ahora);

        // 2. Totales Generales
        snapshot.setTotalHechos((long) todosLosHechos.size());
        snapshot.setHechosVerificados(todosLosHechos.stream()
                .filter(h -> !Boolean.TRUE.equals(h.getFueEliminado())).count());

        // 3. Spam
        if (!solicitudes.isEmpty()) {
            long spam = solicitudes.stream().filter(s -> Boolean.TRUE.equals(s.getEsSpam())).count();
            SolicitudesEliminacionSpam subSpam = new SolicitudesEliminacionSpam();
            subSpam.setTotal((long) solicitudes.size());
            subSpam.setSpam(spam);
            snapshot.setSolicitudesEliminacionSpam(subSpam);
        }

        // 4. Dona de Categorías (Global)
        Map<String, Long> globalCat = todosLosHechos.stream()
                .filter(h -> !Boolean.TRUE.equals(h.getFueEliminado()) && h.getCategoria() != null)
                .collect(Collectors.groupingBy(h -> h.getCategoria().getNombre().trim().toLowerCase(), Collectors.counting()));

        globalCat.entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(top -> {
            CategoriaTopGlobal ctg = new CategoriaTopGlobal();
            ctg.setCategoriaGanadora(top.getKey());
            ctg.setCantidadGanadora(top.getValue());
            ctg.setCantidadPorCategoria(globalCat);
            snapshot.setCategoriaTopGlobal(ctg);
        });

        // 5. Provincias (Global - Usando campo HechosPorProvinciaEnColeccion)
        Map<String, Long> globalProv = todosLosHechos.stream()
                .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
                // FILTRO COMENTADO PARA VER TUS DATOS:
                // .filter(h -> !h.getUbicacion().getProvincia().equalsIgnoreCase("ubicacion externa"))
                .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()));

        if (!globalProv.isEmpty()) {
            Map.Entry<String, Long> top = globalProv.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow();
            HechosPorProvinciaEnColeccion sub = new HechosPorProvinciaEnColeccion();
            sub.setColeccionHandle("GLOBAL");
            sub.setProvinciaGanadora(top.getKey());
            sub.setCantidadGanadora(top.getValue());
            sub.setCantidadPorProvincia(globalProv);
            snapshot.setHechosPorProvinciaEnColeccion(sub);
        }

        // 6. Horarios (Global - Usando campo HorarioPicoPorCategoria)
        Map<Integer, Long> globalHoras = todosLosHechos.stream()
                .filter(h -> h.getFechaYHoraAcontecimiento() != null)
                .collect(Collectors.groupingBy(h -> h.getFechaYHoraAcontecimiento().getHour(), Collectors.counting()));

        if (!globalHoras.isEmpty()) {
            Map.Entry<Integer, Long> top = globalHoras.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow();
            HorarioPicoPorCategoria sub = new HorarioPicoPorCategoria();
            sub.setCategoria("GLOBAL");
            sub.setHoraGanadora(top.getKey());
            sub.setCantidadGanadora(top.getValue());
            sub.setCantidadPorHora(globalHoras);
            snapshot.setHorarioPicoPorCategoria(sub);
        }

        // 7. GUARDAR UNA SOLA VEZ
        estadisticaRepository.save(snapshot);
        System.out.println(">>> SNAPSHOT UNIFICADO GUARDADO.");
    }

    // --- Métodos Auxiliares ---
    public List<Hecho> obtenerTodosLosHechos() {
        try {
            List<HechoOutputDTO> dtos = webClient.get().uri(agregadorUrl + "/hechos")
                    .retrieve().bodyToFlux(HechoOutputDTO.class).collectList().block();
            return dtos == null ? List.of() : dtos.stream().map(this::convertirAEntidad).collect(Collectors.toList());
        } catch (Exception e) { return List.of(); }
    }
    public List<SolicitudDeEliminacion> obtenerSolicitudes() {
        try {
            return webClient.get().uri(agregadorUrl + "/solicitudes")
                    .retrieve().bodyToFlux(SolicitudDeEliminacion.class).collectList().block();
        } catch (Exception e) { return List.of(); }
    }
    private Hecho convertirAEntidad(HechoOutputDTO dto) {
        Hecho h = new Hecho();
        h.setTitulo(dto.getTitulo());
        h.setDescripcion(dto.getDescripcion());
        h.setFechaYHoraAcontecimiento(dto.getFechaAcontecimiento());
        h.setFueEliminado(dto.getFueEliminado());
        if(dto.getCategoria() != null) { Categoria c = new Categoria(); c.setNombre(dto.getCategoria()); h.setCategoria(c); }
        if(dto.getProvincia() != null) { Ubicacion u = new Ubicacion(); u.setProvincia(dto.getProvincia()); h.setUbicacion(u); }
        return h;
    }
}