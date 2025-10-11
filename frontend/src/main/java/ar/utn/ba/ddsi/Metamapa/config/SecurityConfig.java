package ar.utn.ba.ddsi.Metamapa.config;

import ar.utn.ba.ddsi.Metamapa.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

   @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login/**", "/signup/**",
                                "/inicio/**", "/landing/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/colecciones/**",
                                "/hechos/**",
                                "/hechos/nuevo/**",
                                "/privacidad/**", "/error/**", "/403/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/inicio?login=ok", true)
                      //.defaultSuccessUrl("/inicio", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  //ruta para cerrar sesion
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

                .exceptionHandling(ex -> ex
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
