package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.OrderDetailsEntity;
import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar los detalles de los pedidos.
 * Este repositorio proporciona métodos para acceder y gestionar los detalles de los pedidos almacenados
 * en la base de datos.
 */
@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Long> {

    /**
     * Encuentra todos los detalles de un pedido dado el objeto de la entidad de pedido.
     *
     * @param order El pedido cuyo detalle se desea obtener.
     * @return Una lista de entidades de detalles de pedido asociadas con el pedido dado.
     */
    List<OrderDetailsEntity> findByOrder(OrderEntity order);
}
