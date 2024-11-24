package org.edu.usco.pw.ms_official.service;


import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Implementación de la interfaz {@link UserDetailsService} que se utiliza para cargar
 * los detalles del usuario desde la base de datos para la autenticación en Spring Security.
 * La clase carga el usuario por su correo electrónico y asigna los roles correspondientes
 * para la autenticación y autorización.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Carga un usuario desde la base de datos por su correo electrónico.
     * Si el usuario no se encuentra, lanza una excepción {@link UsernameNotFoundException}.
     *
     * @param email el correo electrónico del usuario que se va a cargar.
     * @return un objeto {@link UserDetails} con los detalles del usuario y sus roles.
     * @throws UsernameNotFoundException si no se encuentra un usuario con el correo electrónico proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con email " + email + " no existe."));

        Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        System.out.println("Usuario encontrado: " + user.getEmail());
        System.out.println("Contraseña almacenada: " + user.getPassword());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * Convierte los roles del usuario en una colección de {@link GrantedAuthority}
     * que Spring Security usa para la autorización.
     *
     * @param roles los roles del usuario que deben ser mapeados a {@link GrantedAuthority}.
     * @return una colección de {@link GrantedAuthority} correspondiente a los roles.
     */
    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<Rol> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
}

