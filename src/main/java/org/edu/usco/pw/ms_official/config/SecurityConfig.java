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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura el codificador de contraseñas utilizando BCrypt.
     * Este bean es utilizado para codificar las contraseñas de los usuarios de manera segura.
     *
     * @return Un objeto BCryptPasswordEncoder que será usado para codificar las contraseñas.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad que define las reglas de acceso a las rutas.
     * Las rutas se protegen de acuerdo con los roles de los usuarios.
     *
     * @param http El objeto HttpSecurity que permite configurar la seguridad.
     * @return La configuración de seguridad construida.
     * @throws Exception Si ocurre un error durante la configuración de la seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        /**
                         * Permite acceso sin autenticación a estas rutas.
                         */
                        .requestMatchers("/register", "/", "/iniciar", "/css/**", "/js/**", "/img/**", "/contacto",  "/change-language/**").permitAll()
                        /**
                         * Solo permite acceso a usuarios con rol ADMIN para rutas /admin/**.
                         */
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        /**
                         * Solo permite acceso a usuarios con rol USER para rutas /user/**.
                         */
                        .requestMatchers("/user/**").hasRole("USER")
                        /**
                         * Cualquier otra solicitud requiere autenticación.
                         */
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        /**
                         * Configura la página de inicio de sesión.
                         */
                        .loginPage("/iniciar")
                        /**
                         * Establece un manejador de éxito de autenticación.
                         */
                        .successHandler(successHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        /**
                         * Configura la URL para cerrar sesión.
                         */
                        .logoutUrl("/logout")
                        /**
                         * URL de redirección después de cerrar sesión.
                         */
                        .logoutSuccessUrl("/")
                        /**
                         * Invalidar sesión después de cerrar sesión.
                         */
                        .invalidateHttpSession(true)
                        /**
                         * Elimina la cookie JSESSIONID.
                         */
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Configura el manejador de éxito de autenticación que se ejecuta después de que un usuario se autentica correctamente.
     * Dependiendo del rol del usuario, se redirige a la página correspondiente.
     *
     * @return Un AuthenticationSuccessHandler que maneja la redirección después de una autenticación exitosa.
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            /**
             * Obtiene los roles (authorities) del usuario autenticado.
             */
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            /**
             * Imprime los roles para depuración.
             */
            for (GrantedAuthority authority : authorities) {
                System.out.println("Authority: " + authority.getAuthority());
            }

            /**
             * Dependiendo del rol, redirige a la página correspondiente.
             */
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    redirectUrl = "/admin"; // Redirige al área de administrador
                    break;
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    redirectUrl = "/user"; // Redirige al área de usuario
                    break;
                }
            }

            /**
             * Redirige al usuario a la URL determinada.
             */
            System.out.println("Redirecting to: " + redirectUrl);
            response.sendRedirect(redirectUrl);
        };
    }
}
