package org.edu.usco.pw.ms_official.service;

import jakarta.transaction.Transactional;
import org.edu.usco.pw.ms_official.model.*;
import org.edu.usco.pw.ms_official.repository.OrderDetailsRepository;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con las órdenes en el sistema.
 * Proporciona métodos para crear, obtener, actualizar y buscar órdenes y sus detalles.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    /**
     * Obtiene todas las órdenes en el sistema.
     *
     * @return una lista de todas las órdenes.
     */
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Encuentra todas las órdenes asociadas a un usuario mediante su número de cédula.
     *
     * @param userCc el número de cédula del usuario.
     * @return una lista de órdenes asociadas al usuario con el número de cédula proporcionado.
     */
    public List<OrderEntity> findByUserCc(Long userCc) {
        return orderRepository.findByUser_Cc(userCc);
    }

    /**
     * Guarda una nueva orden en el sistema.
     *
     * @param order la orden que se va a guardar.
     * @return la orden guardada.
     */
    @Transactional
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    /**
     * Encuentra la orden pendiente asociada a un usuario.
     *
     * @param user el usuario que realiza la consulta.
     * @return un Optional que contiene la orden pendiente si existe, de lo contrario, está vacío.
     */
    public Optional<OrderEntity> findPendingOrderByUser(UserEntity user) {
        return orderRepository.findByUserAndStatus(user, "PENDING");
    }

    /**
     * Guarda un detalle de orden en el sistema.
     *
     * @param orderDetail el detalle de la orden que se va a guardar.
     * @return el detalle de la orden guardado.
     */
    public OrderDetailsEntity saveOrderDetail(OrderDetailsEntity orderDetail) {
        return orderDetailsRepository.save(orderDetail);
    }

    /**
     * Encuentra una orden por su ID.
     *
     * @param orderId el ID de la orden.
     * @return un Optional que contiene la orden si existe, de lo contrario, está vacío.
     */
    public Optional<OrderEntity> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Marca una orden como recibida, actualizando su estado a "RECEIVED".
     *
     * @param orderId el ID de la orden a marcar como recibida.
     * @throws RuntimeException si la orden no se encuentra o si su estado no es "SENT".
     */
    public void markAsReceived(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if ("SENT".equals(order.getStatus())) {
            order.setStatus("RECEIVED");
            orderRepository.save(order);
        }
    }

    /**
     * Encuentra todas las órdenes asociadas a un usuario mediante su correo electrónico.
     *
     * @param userEmail el correo electrónico del usuario.
     * @return una lista de órdenes asociadas al correo electrónico proporcionado.
     */
    public List<OrderEntity> findByUserEmail(String userEmail) {
        return orderRepository.findByUserEmail(userEmail);
    }

}
