package org.edu.usco.pw.ms_official.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

@Configuration  // Indica que esta clase es una configuración de Spring
@EnableWebSecurity  // Habilita la seguridad web de Spring
public class SecurityConfig {

    // Bean que configura el codificador de contraseñas usando BCrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Retorna un codificador de contraseñas BCrypt
    }

    // Bean que configura la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acceso sin autenticación a estas rutas
                        .requestMatchers("/register", "/", "/iniciar", "/css/**", "/js/**", "/img/**", "/contacto",  "/change-language/**"  ).permitAll()
                        // Solo permite acceso a usuarios con rol ADMIN para rutas /admin/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Solo permite acceso a usuarios con rol USER para rutas /user/**
                        .requestMatchers("/user/**").hasRole("USER")
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Configura la página de inicio de sesión
                        .loginPage("/iniciar")
                        // Establece un manejador de éxito de autenticación
                        .successHandler(successHandler())
                        .permitAll()  // Permite el acceso a la página de inicio de sesión
                )
                .logout(logout -> logout
                        // Configura la URL para cerrar sesión
                        .logoutUrl("/logout")
                        // URL de redirección después de cerrar sesión
                        .logoutSuccessUrl("/")
                        // Invalidar sesión después de cerrar sesión
                        .invalidateHttpSession(true)
                        // Elimina la cookie JSESSIONID
                        .deleteCookies("JSESSIONID")
                        .permitAll()  // Permite el acceso al cierre de sesión
                );

        return http.build();  // Construye y retorna la configuración de seguridad
    }

    // Bean que configura el manejador de éxito de autenticación
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // Obtiene los roles (authorities) del usuario autenticado
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String redirectUrl = "/";  // URL por defecto para redirigir al usuario

            // Imprime los roles para depuración
            for (GrantedAuthority authority : authorities) {
                System.out.println("Authority: " + authority.getAuthority());
            }

            // Dependiendo del rol, redirige a la página correspondiente
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    redirectUrl = "/admin";  // Redirige al área de administrador
                    break;
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    redirectUrl = "/user";  // Redirige al área de usuario
                    break;
                }
            }

            // Redirige al usuario a la URL determinada
            System.out.println("Redirecting to: " + redirectUrl);
            response.sendRedirect(redirectUrl);  // Realiza la redirección
        };
    }
}
