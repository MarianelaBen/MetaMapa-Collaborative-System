/*
package ar.utn.ba.ddsi.Metamapa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos y login público
                        .requestMatchers("/login", "/signup","/landing",
                                "/css/**", "/js/**", "/images/**",
                                "/colecciones", "/hechos/*", "/colecciones/*",
                                "/hechos/*/
//solicitud", "/hechos/nuevo", "/privacidad").permitAll()
                        // Ejemplo: Acceso a alumnos: ADMIN y DOCENTE
                        //.requestMatchers("/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                        // Lo demás requiere autenticación
                        /*.anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/landing", true) 
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // redirigir tras logout
                        .permitAll()
                )
                /* el de chat:
                  .logout(l -> l.logoutUrl("/logout")
                   .logoutSuccessUrl("/login?logout").permitAll())
                    .rememberMe(r -> r.tokenValiditySeconds(1209600)) // 14 días
                      .sessionManagement(s -> s
                        .sessionFixation().migrateSession()
                         .maximumSessions(1) // evita cuentas compartidas
                        )
                        .csrf(csrf -> csrf
                        // si tenés endpoints REST JSON en este mismo proyecto:
                        //.ignoringRequestMatchers("/api/**")
                        )
                 */

              /*  .exceptionHandling(ex -> ex
                        // Usuario no autenticado → redirigir a login
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized")
                        )
                        // Usuario autenticado pero sin permisos → redirigir a página de error
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403")
                        )
                );

        return http.build();
    }
}
*/