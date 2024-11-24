package org.edu.usco.pw.ms_official.repository;


import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las entidades de usuario.
 * Este repositorio proporciona métodos para acceder y gestionar la información de los usuarios en la base de datos.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Busca un usuario por su cédula.
     *
     * @param cc La cédula del usuario.
     * @return Un usuario opcional que puede estar presente o no en la base de datos.
     */
    Optional<UserEntity> findBycc(Long cc);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return Un usuario opcional que puede estar presente o no en la base de datos.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Busca un usuario por su cédula.
     *
     * @param usercc La cédula del usuario.
     * @return El usuario correspondiente a la cédula proporcionada.
     */
    UserEntity findUserByCc(Long usercc);

    /**
     * Verifica si existe un usuario con el correo electrónico proporcionado.
     *
     * @param email El correo electrónico que se desea verificar.
     * @return `true` si el correo electrónico ya está registrado, `false` si no.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su nombre.
     *
     * @param name El nombre del usuario.
     * @return Un usuario opcional que puede estar presente o no en la base de datos.
     */
    Optional<UserEntity> findByName(String name);

    /**
     * Busca usuarios por su cédula.
     *
     * @param userCc La cédula del usuario.
     * @return Una lista de usuarios con la cédula proporcionada.
     */
    List<UserEntity> findByCc(Long userCc);

    /**
     * Busca usuarios por su nombre o correo electrónico, utilizando una búsqueda parcial.
     *
     * @param name El nombre del usuario.
     * @param email El correo electrónico del usuario.
     * @return Una lista de usuarios cuyo nombre o correo electrónico contienen el término de búsqueda.
     */
    List<UserEntity> findByNameContainingOrEmailContaining(String name, String email);
}