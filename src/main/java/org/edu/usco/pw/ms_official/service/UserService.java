package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.edu.usco.pw.ms_official.repository.RolRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Servicio que maneja la lógica de negocio relacionada con los usuarios, como el registro,
 * la recuperación y la gestión de usuarios en la base de datos. Esta clase interactúa con
 * los repositorios de {@link UserRepository} y {@link RolRepository} para realizar operaciones
 * sobre los usuarios y roles en el sistema.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema. Si el correo electrónico ya está registrado,
     * se lanza una excepción.
     *
     * @param cc el número de cédula del usuario.
     * @param name el nombre completo del usuario.
     * @param email el correo electrónico del usuario.
     * @param password la contraseña del usuario (será codificada).
     * @param phone el número de teléfono del usuario.
     * @param address la dirección del usuario.
     * @return el usuario registrado.
     * @throws RuntimeException si el correo electrónico ya está registrado.
     */
    public UserEntity registerUser(Long cc, String name, String email, String password, String phone, String address) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        UserEntity user = new UserEntity();
        user.setCc(cc);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRegistrationDate(LocalDateTime.now());

        Optional<Rol> defaultRole = roleRepository.findByName("USER");
        Rol userRole = defaultRole.orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    /**
     * Recupera todos los usuarios registrados en el sistema.
     *
     * @return una lista de todos los usuarios.
     */
    public List<UserEntity> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo electrónico del usuario que se busca.
     * @return el usuario encontrado.
     * @throws UsernameNotFoundException si no se encuentra un usuario con el correo dado.
     */
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Recupera un usuario por su correo electrónico. Lanza una excepción si no se encuentra.
     *
     * @param email el correo electrónico del usuario.
     * @return el usuario encontrado.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    /**
     * Busca usuarios por el número de cédula.
     *
     * @param userCc el número de cédula del usuario.
     * @return una lista de usuarios con la cédula dada.
     */
    public List<UserEntity> findByCc(Long userCc) {
        return userRepository.findByCc(userCc);
    }

    /**
     * Busca usuarios por nombre o correo electrónico que contengan el término de búsqueda.
     *
     * @param search el término de búsqueda.
     * @return una lista de usuarios cuyo nombre o correo contienen el término de búsqueda.
     */
    public List<UserEntity> findByNameOrEmailContaining(String search) {
        return userRepository.findByNameContainingOrEmailContaining(search, search);
    }

    /**
     * Recupera todos los usuarios ordenados por número de cédula.
     *
     * @return una lista de usuarios ordenados por número de cédula.
     */
    public List<UserEntity> getAllUsersSortedByCc() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "cc"));
    }

    /**
     * Recupera todos los usuarios ordenados por nombre.
     *
     * @return una lista de usuarios ordenados por nombre.
     */
    public List<UserEntity> getAllUsersSortedByName() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Recupera todos los usuarios ordenados por correo electrónico.
     *
     * @return una lista de usuarios ordenados por correo electrónico.
     */
    public List<UserEntity> getAllUsersSortedByEmail() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "email"));
    }

}




