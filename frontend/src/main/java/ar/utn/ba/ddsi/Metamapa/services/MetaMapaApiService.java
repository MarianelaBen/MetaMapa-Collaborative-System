/*package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.models.dtos.AuthResponseDTO;

import ar.utn.ba.ddsi.Metamapa.models.dtos.ResumenDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.RegisterRequestDTO;
import ar.utn.ba.ddsi.Metamapa.models.dtos.RolesPermisosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class MetaMapaApiService {

    private static final Logger log = LoggerFactory.getLogger(MetaMapaApiService.class);
    private final WebClient webClient;
    private final WebApiCallerService webApiCallerService;
    private final String authServiceUrl;
    private final String baseAdminUrl;

    @Autowired
    public MetaMapaApiService(
            WebApiCallerService webApiCallerService,
            @Value("${auth.service.url}") String authServiceUrl,
            @Value("${backend.api.base-url}") String baseAdminUrl) {
        this.webClient = WebClient.builder().build();
        this.webApiCallerService = webApiCallerService;
        this.authServiceUrl = authServiceUrl;
        this.baseAdminUrl = baseAdminUrl;
    }

    public AuthResponseDTO login(String username, String password) {
        try {
            AuthResponseDTO response = webClient
                    .post()
                    .uri(authServiceUrl + "/auth")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
            return response;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Login fallido - credenciales incorrectas
                return null;
            }
            // Otros errores HTTP
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }


    public AuthResponseDTO signupAndGetTokens(RegisterRequestDTO dto) {
        return webClient.post()
                .uri(authServiceUrl + "/auth/register")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(AuthResponseDTO.class)
                .block();
    }


    public RolesPermisosDTO getRolesPermisos(String accessToken) {
        try {
            RolesPermisosDTO response = webApiCallerService.getWithAuth(
                    authServiceUrl + "/auth/user/roles-permisos",
                    accessToken,
                    RolesPermisosDTO.class
            );
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
        }
    }

    public ResumenDTO getPanelDeControl() {
        String accesToken = webApiCallerService.getAccessTokenFromSession();
        return webApiCallerService.getWithAuth(
            baseAdminUrl + "/resumen", //el metodo que voy a agregar al back
            accesToken,
            ResumenDTO.class
        );
    }

   // ESTO ES DE CHAT GPT:

/*
    //   OPERACIONES PROTEGIDAS (Requieren token)


    public void eliminarHecho(String idHecho, String accessToken) {
        webApiCallerService.delete(agregadorServiceUrl + "/hechos/" + idHecho, //TODO tiene que llamar al agregador
                accessToken
        );
    }

    public SolicitudDTO aprobarSolicitud(String idSolicitud, String accessToken) {
        return api.postWithAuth(
                agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/aprobar",
                accessToken,
                null,
                SolicitudDTO.class
        );
    }

    public SolicitudDTO rechazarSolicitud(String idSolicitud, String accessToken) {
        return api.postWithAuth(
                agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/rechazar",
                accessToken,
                null,
                SolicitudDTO.class
        );
    }

    //===================== HECHOS (PROTEGIDO) =====================


 public HechoDTO actualizarHecho(String id, HechoDTO body, String accessToken) {
        HechoDTO dto = webApiCallerService.putWithAuth(
                agregadorServiceUrl + "/hechos/" + id,
                accessToken,
                body,
                HechoDTO.class
        );
        if (dto == null) throw new RuntimeException("Error al actualizar el Hecho");
        return dto;
    }



     // ===================== SOLICITUDES DE ELIMINACIÓN =====================


    // Listar pendientes (protegido para ADMIN)

    public List<SolicitudDTO> listarSolicitudesPendientes(String accessToken) {
        String url = agregadorServiceUrl + "/solicitudes?estado=PENDIENTE";
        return webApiCallerService.getListWithAuth(url, accessToken, SolicitudDTO.class);
    }


    public SolicitudDTO aprobarSolicitud(String id, String accessToken) {
        return webApiCallerService.postWithAuth(
                agregadorServiceUrl + "/solicitudes/" + id + "/aprobar",
                accessToken,
                null,
                SolicitudDTO.class
        );
    }

    public SolicitudDTO rechazarSolicitud(String id, String accessToken) {
        return webApiCallerService.postWithAuth(
                agregadorServiceUrl + "/solicitudes/" + id + "/rechazar",
                accessToken,
                null,
                SolicitudDTO.class
        );
    }

*/
/*
}

*/
