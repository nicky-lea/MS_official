package org.edu.usco.pw.ms_official.repository;


import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBycc(Long cc);
    Optional<UserEntity> findByEmail(String email);
    UserEntity findUserByCc(Long usercc);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByName(String name);
    List<UserEntity> findByCc(Long userCc);
    List<UserEntity> findByNameContainingOrEmailContaining(String name, String email);
}