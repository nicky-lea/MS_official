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

/**
 * Repositorio para gestionar las órdenes de los usuarios.
 * Este repositorio proporciona métodos para acceder y gestionar las órdenes almacenadas
 * en la base de datos, incluidos los detalles de las órdenes y los informes de ventas.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Encuentra todas las órdenes de un usuario por su número de cédula.
     *
     * @param userCc El número de cédula del usuario.
     * @return Una lista de órdenes asociadas con el número de cédula del usuario.
     */
    List<OrderEntity> findByUser_Cc(Long userCc);

    /**
     * Encuentra una orden pendiente o en otro estado por el usuario.
     *
     * @param user El usuario que ha realizado la orden.
     * @param status El estado de la orden.
     * @return Una orden asociada con el usuario y estado especificado.
     */
    Optional<OrderEntity> findByUserAndStatus(UserEntity user, String status);

    /**
     * Encuentra todas las órdenes realizadas por un usuario mediante su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return Una lista de órdenes asociadas con el correo electrónico del usuario.
     */
    List<OrderEntity> findByUserEmail(String email);

    /**
     * Genera un informe de ventas de productos entre dos fechas.
     * Este método devuelve un reporte de los productos vendidos, la cantidad total y el monto total de ventas.
     *
     * @param startDate Fecha de inicio del periodo del informe.
     * @param endDate Fecha de fin del periodo del informe.
     * @return Una lista de resultados con el nombre del producto, la cantidad total vendida y el monto total de ventas.
     */
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
