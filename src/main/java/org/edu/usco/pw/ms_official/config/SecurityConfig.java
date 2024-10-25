package org.edu.usco.pw.ms_official.config;

import org.edu.usco.pw.ms_official.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

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
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                .formLogin(form -> form
                        .loginPage("/iniciar")
                        .successHandler(successHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    redirectUrl = "/admin";
                    break;
                }else if (authority.getAuthority().equals("ROLE_USER")) {
                    redirectUrl = "/user";
                    break;
                }
            }

            response.sendRedirect(redirectUrl);
        };

    }
    }