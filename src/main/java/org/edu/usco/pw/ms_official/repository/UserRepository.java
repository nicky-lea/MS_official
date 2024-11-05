package org.edu.usco.pw.ms_official.repository;


import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    // Validar si existe email
    boolean existsByEmail(String email);

    List<UserEntity> findByCc(Long userCc);
    List<UserEntity> findByNameContainingOrEmailContaining(String name, String email);
}