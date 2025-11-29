package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ContribuyenteDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
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

    @PersistenceContext
    private EntityManager em; //TODO PARA PRUEBAS PORQUE NO HAY CONTR REPO TDV

    @Override
    @Transactional //TODO PARA PRUEBAS PORQUE NO HAY CONTR REPO TDV
    public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
        if (fuentes == null || fuentes.isEmpty()) {
            throw new IllegalArgumentException("No se especificaron fuentes.");
        }
        List<Hecho> hechos = fuentes.stream()
                .flatMap(f -> obtenerTodosLosHechosDeFuente(f)
                        .stream())
                .map(h -> {
                    System.out.println(h);
                    try { //para que si falla la normalizacion de un hecho no falle toda la normalizacion
                        normalizadorService.normalizar(h);
                        return h;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (hechos.isEmpty()) {
            throw new NoSuchElementException("No se encontraron hechos para las fuentes indicadas.");
        }

        hechoRepository.saveAll(hechos); //TODO COMO EVITAR DUPLICADOS
        return hechos;
    }

    @Override
    public List<HechoOutputDTO> obtenerHechos() {
        return this.hechoRepository.findAll().stream()
                .map(this::hechoOutputDTO)
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
            contrDto.setId(hecho.getContribuyente().getId());
            contrDto.setNombre(hecho.getContribuyente().getNombre());
            contrDto.setApellido(hecho.getContribuyente().getApellido());
            contrDto.setFechaDeNacimiento(hecho.getContribuyente().getFechaDeNacimiento());
            hechoOutputDTO.setContribuyente(contrDto);
        }
        if (hecho.getFuenteExterna() != null) {
            hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
        }

        // Cálculo edición: 7 días desde fechaCarga
//    LocalDate fc = hecho.getFechaCarga();
//    if (fc != null) {
//      long dias = ChronoUnit.DAYS.between(fc, LocalDate.now());
//      boolean editable = dias < 7;
//      hechoOutputDTO.setEditable(editable);
//      hechoOutputDTO.setDiasRestantesEdicion(editable ? (int) (7 - dias) : 0);
//    } else {
//      hechoOutputDTO.setEditable(false);
//      hechoOutputDTO.setDiasRestantesEdicion(0);
//    }

        return hechoOutputDTO;
    }


    //API PUBLICA

    @Override
    public List<HechoOutputDTO> obtenerHechosPorColeccion(
            String handle,
            TipoDeModoNavegacion modo,
            String categoria,
            String fuente,
            String ubicacion,
            String keyword,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    ) {

        Coleccion coleccionInfo = coleccionService.findById(handle);
        TipoAlgoritmoDeConsenso algoritmoActual = coleccionInfo.getAlgoritmoDeConsenso();

        List<Hecho> hechos = coleccionService.obtenerHechosPorColeccion(handle, modo);

        if (hechos == null) {
            throw new NoSuchElementException("Coleccion no encontrada: " + handle);
        }

        return hechos.stream()
                .filter(hecho -> cumpleFiltros(hecho, categoria, fuente, ubicacion, keyword, fechaDesde, fechaHasta))
                .map(hecho -> mapearConConsenso(hecho, algoritmoActual))
                .toList();
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
}