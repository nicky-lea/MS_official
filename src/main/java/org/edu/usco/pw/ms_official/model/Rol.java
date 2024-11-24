package org.edu.usco.pw.ms_official.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un rol asignado a los usuarios en el sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único del rol", example = "1")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    @Schema(description = "Nombre del rol (por ejemplo, ADMIN, USER)", example = "ADMIN")
    private String name;

    @ManyToMany(mappedBy = "roles")
    @Schema(description = "Lista de usuarios asignados a este rol")
    @ArraySchema(schema = @Schema(implementation = UserEntity.class))
    private List<UserEntity> users = new ArrayList<>();
    public Rol(String name) {
        this.name = name;
    }
}