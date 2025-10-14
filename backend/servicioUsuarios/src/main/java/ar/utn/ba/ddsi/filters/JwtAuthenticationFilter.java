package ar.utn.ba.ddsi.filters;


import ar.utn.ba.ddsi.models.entities.Usuario;
import ar.utn.ba.ddsi.models.enums.Rol;
import ar.utn.ba.ddsi.models.repositories.IUsuariosRepository;
import ar.utn.ba.ddsi.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IUsuariosRepository usuarioRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization"); //se fija si tenes el header autorization
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = JwtUtil.validarToken(token);  //trata de validar tu token y devuelve el username

                Usuario user = usuarioRepo.findByNombreDeUsuario(username)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Rol rolUsuario = user.getRol();
                var auth = new UsernamePasswordAuthenticationToken( //crea un objeto de autenticacion
                        username,
                        null, //sin contraseña
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rolUsuario.toString())) //con sus roles
                );                              //(new SimpleGrantedAuthority("ROLE_" + rolUsuario.name()))
                SecurityContextHolder.getContext().setAuthentication(auth); //una vez que tenemos al usuario logueado con sus datos lo seteamos en el contexto
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        } else {
            System.out.println("No hay token de autorización");
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) return true; // saltar filtro para web
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight
        return path.equals("/api/auth") || path.equals("/api/auth/refresh");
    }
    /* METODO ORIGINAL
    @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getRequestURI();
            // No aplicar el filtro JWT solo a los endpoints públicos de autenticación
            return path.equals("/api/auth") || path.equals("/api/auth/refresh");
    }*/

}