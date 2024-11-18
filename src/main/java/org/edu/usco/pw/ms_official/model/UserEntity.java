package org.edu.usco.pw.ms_official.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa a un usuario en el sistema")
public class UserEntity {

    @Id
    @Column(name = "cc", nullable = false)
    @Schema(description = "Cédula de ciudadanía del usuario (ID único)", example = "1234567890")
    private Long cc;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    @Schema(description = "Contraseña del usuario (encriptada)", example = "password@123")
    private String password;

    @Column(name = "phone", length = 20)
    @Schema(description = "Número de teléfono del usuario", example = "3001234567")
    private String phone;

    @Column(name = "address", length = 255)
    @Schema(description = "Dirección del usuario", example = "Calle 123, Bogotá")
    private String address;

    @Column(name = "registration_date")
    @Schema(description = "Fecha y hora de registro del usuario", example = "2024-11-17T15:30:00")
    private LocalDateTime registrationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_cc"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Schema(description = "Roles asociados al usuario")
    @ArraySchema(schema = @Schema(implementation = Rol.class))
    private List<Rol> roles = new ArrayList<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
}
