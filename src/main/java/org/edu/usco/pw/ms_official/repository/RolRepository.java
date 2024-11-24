package org.edu.usco.pw.ms_official.repository;


import org.edu.usco.pw.ms_official.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar los roles de los usuarios.
 * Este repositorio proporciona métodos para acceder y gestionar los roles en la base de datos.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param name El nombre del rol que se debe buscar.
     * @return Un rol opcional que puede estar presente o no en la base de datos.
     */
    Optional<Rol> findByName(String name);
}