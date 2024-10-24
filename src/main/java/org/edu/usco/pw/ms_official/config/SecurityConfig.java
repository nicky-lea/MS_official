package org.edu.usco.pw.ms_official.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register","/", "/iniciar", "/css/**", "/js/**", "/img/**").permitAll() // Permitir acceso a estas rutas sin autenticación
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                .formLogin(form -> form
                        .loginPage("/iniciar") // Página de inicio de sesión personalizada
                        .permitAll() // Permitir acceso a la página de inicio de sesión para todos
                )
                .logout(logout -> logout
                        .permitAll() // Permitir que todos puedan cerrar sesión
                );

        return http.build();
    }
}