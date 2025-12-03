package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.*;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {
    @Autowired
    IColeccionService coleccionService;
    private final AdapterFuenteDinamica adapterFuenteDinamica;
    private final AdapterFuenteEstatica adapterFuenteEstatica;
    private final AdapterFuenteProxy adapterFuenteProxy;
    private final NormalizadorService normalizadorService;
    private final IHechoRepository hechoRepository;
    private final ICategoriaRepository categoriaRepository; //TODO BORRAR CUANDO SE ARREGLE NORMALIZADOR
    private final ISolicitudRepository solicitudesRepo;
    private final IColeccionRepository coleccionRepo;

    public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica, AdapterFuenteEstatica adapterFuenteEstatica, AdapterFuenteProxy adapterFuenteProxy, NormalizadorService normalizadorService, IHechoRepository hechoRepository, ICategoriaRepository categoriaRepository, ISolicitudRepository solicitudesRepo, IColeccionRepository coleccionRepo) {
        this.adapterFuenteDinamica = adapterFuenteDinamica;
        this.adapterFuenteEstatica = adapterFuenteEstatica;
        this.adapterFuenteProxy = adapterFuenteProxy;
        this.normalizadorService = normalizadorService;
        this.hechoRepository = hechoRepository;
        this.categoriaRepository = categoriaRepository;
        this.solicitudesRepo = solicitudesRepo;
        this.coleccionRepo = coleccionRepo;
    }

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    @Override
    public List<SolicitudOutputDTO> getSolicitudes() {
        return solicitudesRepo.findAll().stream()
                .map(SolicitudOutputDTO::fromEntity) //Convertimos cada entidad a DTO
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
        if (fuentes == null || fuentes.isEmpty()) {
            throw new IllegalArgumentException("No se especificaron fuentes.");
        }

        List<Hecho> hechosNuevos = fuentes.stream()

                .flatMap(f -> {
                    try {
                        return obtenerTodosLosHechosDeFuente(f).stream();
                    } catch (Exception e) {
                        System.err.println("⚠️ ALERTA: Falló la fuente " + f.getUrl() + " (" + f.getTipo() + "). Causa: " + e.getMessage());
                        return java.util.stream.Stream.empty();
                    }
                })

                .map(h -> {
                    try {
                        normalizadorService.normalizar(h);
                        return h;
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error normalizando hecho: " + h.getTitulo());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (hechosNuevos.isEmpty()) {
            System.err.println("No se pudieron obtener hechos nuevos (o todas las fuentes fallaron).");
            return new ArrayList<>();
        }

        List<Hecho> hechosParaGuardar = new ArrayList<>();

        for (Hecho hechoNuevo : hechosNuevos) {
            Optional<Hecho> hechoExistente = hechoRepository.findByTitulo(hechoNuevo.getTitulo());

            if (hechoExistente.isPresent()) {
                Hecho existente = hechoExistente.get();
                existente.setFechaCarga(LocalDate.now());
                existente.setFueEliminado(false);
                hechosParaGuardar.add(existente);
            } else {
                hechosParaGuardar.add(hechoNuevo);
            }
        }

        hechoRepository.saveAll(hechosParaGuardar);

        return hechosParaGuardar;
    }
    @Override
    @Transactional(readOnly = true)
    public List<HechoOutputDTO> obtenerHechos() {
        return this.hechoRepository.findAll().stream()
                .map(this::hechoOutputDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HechoOutputDTO> obtenerHechosPorContribuyente(Long contribuyenteId) {
        List<Hecho> hechos = hechoRepository.buscarPorIdContribuyente(contribuyenteId);

        return hechos.stream()
            .map(this::hechoOutputDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<HechoOutputDTO> obtenerHechosPorContribuyenteFiltrado(
        Long contribuyenteId,
        String titulo,
        String categoria,
        String estado
    ) {
        List<Hecho> hechos = hechoRepository.buscarPorIdContribuyente(contribuyenteId);

        // Primero mapeamos todo (esto ya calcula editable y diasRestantes)
        List<HechoOutputDTO> dtos = hechos.stream()
            .map(this::hechoOutputDTO)
            .collect(Collectors.toList());

        // Luego filtramos sobre los DTO
        return dtos.stream()
            .filter(dto -> {
                // FILTRO TITULO
                if (titulo != null && !titulo.isBlank()) {
                    if (dto.getTitulo() == null ||
                        !dto.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                        return false;
                    }
                }

                // FILTRO CATEGORIA
                if (categoria != null && !categoria.isBlank()) {
                    if (dto.getCategoria() == null ||
                        !dto.getCategoria().equalsIgnoreCase(categoria)) {
                        return false;
                    }
                }

                // FILTRO ESTADO (editable / expirado)
                if (estado != null && !estado.isBlank()) {
                    boolean esEditable = dto.isEditable(); // viene ya calculado

                    if ("editable".equalsIgnoreCase(estado) && !esEditable) {
                        return false;
                    }
                    if ("expirado".equalsIgnoreCase(estado) && esEditable) {
                        return false;
                    }
                }

                return true;
            })
            .collect(Collectors.toList());
    }



    @Override
    public Page<HechoOutputDTO> obtenerHechosConPaginacion(Pageable pageable) {
        return hechoRepository.findAll(pageable).map(this::hechoOutputDTO);
    }

    @Override
    public void sumarVistaColeccion(String handle) {
        Coleccion c = coleccionRepo.findByHandle(handle)
                .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + handle));

        Integer actuales = c.getCantVistas();
        if (actuales == null) actuales = 0;
        c.setCantVistas(actuales + 1);

        coleccionRepo.save(c);
    }

    @Override
    public void sumarVistaHecho(Long id) {
        Hecho h = hechoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + id));

        Integer actuales = h.getCantVistas();
        if (actuales == null) actuales = 0;
        h.setCantVistas(actuales + 1);

        hechoRepository.save(h);
    }

    public List<HechoOutputDTO> top3Hechos() {
        List<Hecho> hechos = this.hechoRepository.findTop3ByFueEliminadoFalseOrderByCantVistasDesc();
        return hechos.stream().map(this::hechoOutputDTO).collect(Collectors.toList());
    }

    public List<ColeccionOutputDTO> top4Colecciones() {
        List<Coleccion> cols = this.coleccionRepo.findTop4ByOrderByCantVistasDesc();
        return cols.stream().map(ColeccionOutputDTO::fromEntity).collect(Collectors.toList());
    }


    private Categoria ensureCategoria(String nombre) { //todo borrar con normlaizacion ya se arregla
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nombre)));
    }

    @Override
    public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente) {
        List<Hecho> hechos = new ArrayList<>();
        switch (fuente.getTipo()) {
            case ESTATICA:
                hechos.addAll(adapterFuenteEstatica.obtenerHechos(fuente.getUrl()));
                break;
            case PROXY:

                hechos.addAll(adapterFuenteProxy.obtenerHechos(fuente.getUrl()));
                break;
            case DINAMICA:

                hechos.addAll(adapterFuenteDinamica.obtenerHechos(fuente.getUrl()));
                break;
        }

        return hechos;
    }

    @Override
    public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
        HechoOutputDTO hechoOutputDTO = new HechoOutputDTO();

        hechoOutputDTO.setCantVistas(hecho.getCantVistas());
        hechoOutputDTO.setId(hecho.getId());
        hechoOutputDTO.setTitulo(hecho.getTitulo());
        hechoOutputDTO.setDescripcion(hecho.getDescripcion());
        hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
        hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
        hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
        hechoOutputDTO.setFueEliminado(hecho.getFueEliminado());
        hechoOutputDTO.setIdEnFuente(hecho.getIdEnFuente());

        if (hecho.getUbicacion() != null) {
            hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
            hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
            hechoOutputDTO.setProvincia(hecho.getUbicacion().getProvincia());
        } else {
            hechoOutputDTO.setLatitud(null);
            hechoOutputDTO.setLongitud(null);
            hechoOutputDTO.setProvincia(null);
        }

        if (hecho.getEtiquetas() != null) {
            hechoOutputDTO.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
        }

        hechoOutputDTO.setIdContenidoMultimedia(
                hecho.getPathMultimedia() != null ? hecho.getPathMultimedia() : List.of()
        );

        if (hechoOutputDTO.getIdContenidoMultimedia() != null) {
            hechoOutputDTO.setIdContenidoMultimedia(
                    hechoOutputDTO.getIdContenidoMultimedia().stream()
                            .filter(Objects::nonNull)
                            .map(name -> {
                                if (name.startsWith("http")) {
                                    return name;
                                }
                                String base = minioPublicUrl.endsWith("/") ? minioPublicUrl : minioPublicUrl + "/";
                                return base + name;
                            })
                            .toList()
            );
        }

        if (hecho.getContribuyente() != null) {
            ContribuyenteDTO contrDto = new ContribuyenteDTO();
            contrDto.setId(hecho.getContribuyente().getIdContribuyente());
            contrDto.setNombre(hecho.getContribuyente().getNombre());
            contrDto.setApellido(hecho.getContribuyente().getApellido());
            contrDto.setFechaDeNacimiento(hecho.getContribuyente().getFechaDeNacimiento());
            hechoOutputDTO.setContribuyente(contrDto);
        }
        if (hecho.getFuenteExterna() != null) {
            hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
        }

        LocalDate fc = hecho.getFechaCarga();
        if (fc != null) {
            long dias = ChronoUnit.DAYS.between(fc, LocalDate.now());
            boolean editable = dias < 7;
            hechoOutputDTO.setEditable(editable);
            hechoOutputDTO.setDiasRestantes(editable ? (int) (7 - dias) : 0);
        } else {
            hechoOutputDTO.setEditable(false);
            hechoOutputDTO.setDiasRestantes(0);
        }

        return hechoOutputDTO;
    }


    //API PUBLICA

// En AgregadorService.java

    @Override
    public PaginaDTO<HechoOutputDTO> obtenerHechosPorColeccion(
            String handle,
            TipoDeModoNavegacion modo,
            String categoria,
            String fuente,
            String ubicacion,
            String keyword,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            int page, int size
    ) {

        Coleccion coleccionInfo = coleccionService.findById(handle);
        TipoAlgoritmoDeConsenso algoritmoActual = coleccionInfo.getAlgoritmoDeConsenso();

        List<Hecho> hechos = coleccionService.obtenerHechosPorColeccion(handle, modo);
        if (hechos == null) throw new NoSuchElementException("Coleccion no encontrada: " + handle);

        List<HechoOutputDTO> listaCompleta = hechos.stream()
                .filter(hecho -> cumpleFiltros(hecho, categoria, fuente, ubicacion, keyword, fechaDesde, fechaHasta))
                .map(hecho -> mapearConConsenso(hecho, algoritmoActual))
                .filter(dto -> {
                    if (TipoDeModoNavegacion.CURADO.equals(modo)) {
                        return Boolean.TRUE.equals(dto.getConsensuado());
                    }
                    return true;
                })
                .toList();

        long totalElements = listaCompleta.size();
        int start = page * size;
        int end = Math.min(start + size, (int) totalElements);

        List<HechoOutputDTO> content;
        if (start >= totalElements) {
            content = new ArrayList<>();
        } else {
            content = listaCompleta.subList(start, end);
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);

        PaginaDTO<HechoOutputDTO> paginaDTO = new PaginaDTO<>();
        paginaDTO.setContent(content);
        paginaDTO.setNumber(page);
        paginaDTO.setSize(size);
        paginaDTO.setTotalElements(totalElements);
        paginaDTO.setTotalPages(totalPages);
        paginaDTO.setNumberOfElements(content.size());
        paginaDTO.setFirst(page == 0);
        paginaDTO.setLast(page >= totalPages - 1);

        return paginaDTO;
    }


    private HechoOutputDTO mapearConConsenso(Hecho hecho, TipoAlgoritmoDeConsenso algoritmo) {
        HechoOutputDTO dto = this.hechoOutputDTO(hecho);

        if (algoritmo == null) {
            dto.setConsensuado(true);
        } else {
            Boolean estaAprobado = hecho.getConsensoPorAlgoritmo().get(algoritmo);
            dto.setConsensuado(Boolean.TRUE.equals(estaAprobado));
        }

        return dto;
    }

    private boolean cumpleFiltros(Hecho hecho, String categoria, String fuente, String ubicacion,
                                  String keyword, LocalDate fechaDesde, LocalDate fechaHasta) {

        if (categoria != null && !categoria.isBlank()) {
            if (hecho.getCategoria() == null ||
                    hecho.getCategoria().getNombre() == null ||
                    !hecho.getCategoria().getNombre().equalsIgnoreCase(categoria)) {
                return false;
            }
        }

        if (fuente != null && !fuente.isBlank()) {
            if (hecho.getOrigen() == null ||
                    !hecho.getOrigen().name().equalsIgnoreCase(fuente)) {
                return false;
            }
        }

        if (ubicacion != null && !ubicacion.isBlank()) {
            if (hecho.getUbicacion() == null || hecho.getUbicacion().getProvincia() == null) {
                return false;
            }

            String prov = hecho.getUbicacion().getProvincia().toLowerCase();
            String filtro = ubicacion.toLowerCase();

            if (!prov.contains(filtro)) {
                return false;
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            String titulo = (hecho.getTitulo() != null) ? hecho.getTitulo().toLowerCase() : "";
            String desc = (hecho.getDescripcion() != null) ? hecho.getDescripcion().toLowerCase() : "";

            if (!titulo.contains(k) && !desc.contains(k)) {
                return false;
            }
        }

        if (fechaDesde != null) {
            if (hecho.getFechaAcontecimiento() == null ||
                    hecho.getFechaAcontecimiento().toLocalDate().isBefore(fechaDesde)) {
                return false;
            }
        }

        if (fechaHasta != null) {
            if (hecho.getFechaAcontecimiento() == null ||
                    hecho.getFechaAcontecimiento().toLocalDate().isAfter(fechaHasta)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public HechoOutputDTO obtenerDetalleHecho(String handle, Long hechoId) {

        // 1. Buscamos la colección para obtener su algoritmo de consenso
        Coleccion coleccionInfo = coleccionService.findById(handle);
        TipoAlgoritmoDeConsenso algoritmoActual = coleccionInfo.getAlgoritmoDeConsenso();

        // 2. Buscamos el hecho crudo (Entidad de dominio)
        // Nota: Necesitas un método en tu ColeccionService (o repositorio) que busque por ID.
        Hecho hecho = coleccionService.obtenerHechoPorId(hechoId);

        if (hecho == null) {
            throw new NoSuchElementException("El hecho con id " + hechoId + " no existe.");
        }

        // 3. Reutilizamos el mapeo inteligente que ya calcula el booleano 'consensuado'
        return mapearConConsenso(hecho, algoritmoActual);
    }

    public PaginaDTO<CategoriaOutputDTO> obtenerPaginado(int page, int size) {
        // 1. Obtener la página de la entidad (JPA)
        Pageable pageable = PageRequest.of(page, size);
        Page<Categoria> paginaEntidad = categoriaRepository.findAll(pageable);

        // 2. Mapear de Entidad a DTO
        // Esto soluciona el warning "Serializing PageImpl instances"
        List<CategoriaOutputDTO> contenidoDTO = paginaEntidad.getContent().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());

        // 3. Construir tu PaginaDTO
        PaginaDTO<CategoriaOutputDTO> paginaDTO = new PaginaDTO<>();
        paginaDTO.setContent(contenidoDTO);
        paginaDTO.setNumber(paginaEntidad.getNumber());
        paginaDTO.setSize(paginaEntidad.getSize());
        paginaDTO.setTotalElements(paginaEntidad.getTotalElements());
        paginaDTO.setTotalPages(paginaEntidad.getTotalPages());
        paginaDTO.setNumberOfElements(paginaEntidad.getNumberOfElements());
        paginaDTO.setFirst(paginaEntidad.isFirst());
        paginaDTO.setLast(paginaEntidad.isLast());

        return paginaDTO;
    }

    private CategoriaOutputDTO mapearADTO(Categoria entidad) {
        CategoriaOutputDTO dto = new CategoriaOutputDTO(entidad.getNombre());
        dto.setId(entidad.getId());
        return dto;
    }
}