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

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderRepository orderRepository;

    public UserEntity findByUser(Long userCc) {
        List<UserEntity> users = userRepository.findByCc(userCc);
        if (users != null && !users.isEmpty()) {
            return users.get(0); // Retorna el primer usuario si la lista no está vacía
        }
        return null; // Si no se encuentra el usuario
    }


    public UserEntity registerUser(Long cc, String name, String email, String password, String phone, String address) {
        // Validar que el email no exista
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

        // Asignar rol por defecto
        Optional<Rol> defaultRole = roleRepository.findByName("USER");
        Rol userRole = defaultRole.orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        // Asignar el rol a la colección de roles
        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    // Método para agregar roles adicionales si es necesario
    public void addRoleToUser(Long cc, String roleName) {
        UserEntity user = userRepository.findById(cc)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    public void updateUser(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserEntity> findByCc(Long userCc) {
        return userRepository.findByCc(userCc);
    }

    public List<UserEntity> findByNameOrEmailContaining(String search) {
        return userRepository.findByNameContainingOrEmailContaining(search, search);
    }
    // Método para obtener todos los usuarios ordenados por CC
    public List<UserEntity> getAllUsersSortedByCc() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "cc"));
    }

    // Método para obtener todos los usuarios ordenados por nombre
    public List<UserEntity> getAllUsersSortedByName() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    // Método para obtener todos los usuarios ordenados por email
    public List<UserEntity> getAllUsersSortedByEmail() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "email"));
    }


}



