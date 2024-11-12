package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser(UserEntity user); // Método para encontrar órdenes por usuario
    List<OrderEntity> findByUser_Cc(Long userCc);
    Optional<OrderEntity> findByUserAndStatus(UserEntity user, String status);
}
