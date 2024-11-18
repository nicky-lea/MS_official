package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser(UserEntity user); // Método para encontrar órdenes por usuario
    List<OrderEntity> findByUser_Cc(Long userCc);
    Optional<OrderEntity> findByUserAndStatus(UserEntity user, String status);
    List<OrderEntity> findByUserEmail(String email);
    @Query("SELECT " +
            "od.product.name AS productName, " +
            "SUM(od.quantity) AS totalQuantity, " +
            "SUM(od.quantity * od.unitPrice) AS totalAmount " +
            "FROM OrderEntity o " +
            "JOIN o.orderDetails od " +
            "WHERE o.status NOT IN ('CANCELLED', 'REFUND', 'PENDING') " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY od.product.name " +
            "ORDER BY totalAmount DESC")
    List<Object[]> findSalesReport(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

}
