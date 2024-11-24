package org.edu.usco.pw.ms_official;


import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.RolRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Clase encargada de cargar datos iniciales en la base de datos al inicio de la aplicación.
 * Esta clase es ejecutada automáticamente al arrancar la aplicación si está configurada como un bean de tipo {@link CommandLineRunner}.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor para inicializar los repositorios necesarios para cargar los datos.
     *
     * @param rolRepository Repositorio para gestionar roles.
     * @param userRepository Repositorio para gestionar usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     */
    public DataLoader(RolRepository rolRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método ejecutado al inicio de la aplicación que carga los roles "ADMIN" y "USER" en la base de datos
     * si no existen previamente. Además, crea un usuario administrador por defecto si no existe un usuario
     * con la cédula 1 en la base de datos.
     *
     * @param args Argumentos de línea de comandos, no utilizados en este caso.
     * @throws Exception Si ocurre algún error durante la ejecución.
     */
    @Override
    public void run(String... args) throws Exception {

        if (rolRepository.findByName("ADMIN").isEmpty()) {
            rolRepository.save(new Rol("ADMIN"));
        }

        if (rolRepository.findByName("USER").isEmpty()) {
            rolRepository.save(new Rol("USER"));
        }

        if (userRepository.findByCc(1L).isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setCc(1L);
            adminUser.setName("AdminMs");
            adminUser.setEmail("adminms@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setPhone("3008020893");
            adminUser.setAddress("Admin Address, Bogotá");
            adminUser.setRegistrationDate(LocalDateTime.now());

            Rol adminRole = rolRepository.findByName("ADMIN").get();
            adminUser.setRoles(Arrays.asList(adminRole));

            userRepository.save(adminUser);
        }
    }
}