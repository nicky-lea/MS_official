package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.User;
import org.edu.usco.pw.ms_official.repository.RolRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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

    @PostMapping("/register")
    public User registerUser(@RequestParam Long cc,
                               @RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String address) {
        // Crear un nuevo objeto User
        User user = new User();
        user.setCc(cc);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Codificar la contraseña
        user.setPhone(phone);
        user.setAddress(address);
        user.setRegistrationDate(LocalDateTime.now());

        // Asignar rol de usuario (por defecto)
        Optional<Rol> role = roleRepository.findByName("USER");
        Rol userRole = role.orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        user.setRole(userRole);

        // Guardar el usuario en la base de datos
        return userRepository.save(user);

    }
}



