package ar.utn.ba.ddsi.Metamapa.services;

import ar.utn.ba.ddsi.Metamapa.models.dtos.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MetaMapaApiService {

    private static final Logger log = LoggerFactory.getLogger(MetaMapaApiService.class);
    private final WebClient webClient;
    private final WebApiCallerService webApiCallerService;
    private final String authServiceUrl;
    private final String baseAdminUrl;
    private final String basePublicUrl;

    @Autowired
    public MetaMapaApiService(
            //backend.api.base-url-agregador=http://localhost:8083/api/public
            WebApiCallerService webApiCallerService,
            @Value("${auth.service.url}") String authServiceUrl,
            @Value("${backend.api.base-url}") String baseAdminUrl,
            @Value("${backend.api.base-url-agregador}") String basePublicUrl) {
        this.webClient = WebClient.builder().build();
        this.webApiCallerService = webApiCallerService;
        this.authServiceUrl = authServiceUrl;
        this.baseAdminUrl = baseAdminUrl;
        this.basePublicUrl = basePublicUrl;
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

    public List<SolicitudDTO> getSolicitudes() {

        String accessToken = webApiCallerService.getAccessTokenFromSession();

        // Pedimos un array de DTOs y lo convertimos a List
        SolicitudDTO[] arr = webApiCallerService.getWithAuth(
                basePublicUrl + "/solicitudes",
                accessToken,
                SolicitudDTO[].class
        );

        if (arr == null || arr.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(arr);
    }

    public List<HechoDTO> getHechos() {

        String accessToken = webApiCallerService.getAccessTokenFromSession();

        // Pedimos un array de DTOs y lo convertimos a List
        HechoDTO[] arr = webApiCallerService.getWithAuth(
                basePublicUrl + "/hechos",
                accessToken,
                HechoDTO[].class
        );

        if (arr == null || arr.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(arr);
    }

    public ColeccionDTO crearColeccion(ColeccionDTO coleccion) {
        return webApiCallerService.post(
                baseAdminUrl + "/colecciones",
                coleccion,
                ColeccionDTO.class
        );
    }

    public ColeccionDTO actualizarColeccion(String handle, ColeccionDTO coleccion){
        return webApiCallerService.put(
                baseAdminUrl + "/colecciones/" + handle,
                coleccion,
                ColeccionDTO.class
        );
    }

    public InformeDeResultadosDTO importarHechosCsv(MultipartFile archivo){
        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.part("archivo", archivo.getResource())
                .filename(archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "import.csv")
                .contentType(MediaType.parseMediaType("text/csv"));
        final String PART_NAME = "archivo";
        return webApiCallerService.postMultipart(
                baseAdminUrl + "/import/hechos/csv",
                PART_NAME,
                archivo,
                InformeDeResultadosDTO.class

        );
    }
}




