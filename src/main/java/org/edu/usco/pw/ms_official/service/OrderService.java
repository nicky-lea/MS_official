package org.edu.usco.pw.ms_official.service;

import jakarta.transaction.Transactional;
import org.edu.usco.pw.ms_official.excepciones.ResourceNotFoundException;
import org.edu.usco.pw.ms_official.model.*;
import org.edu.usco.pw.ms_official.repository.OrderDetailsRepository;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


//    public void updateOrderStatus(Long orderId, String status) {
//        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//        order.setStatus(status);
//        orderRepository.save(order);
//    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll(); // Obtiene todos los pedidos
    }

    public List<OrderEntity> findByUserCc(Long userCc) {
        return orderRepository.findByUser_Cc(userCc); // Implementa el método en el repositorio
    }

    @Transactional
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    public Optional<OrderEntity> findPendingOrderByUser(UserEntity user) {
        return orderRepository.findByUserAndStatus(user, "PENDING");
    }

    public OrderDetailsEntity saveOrderDetail(OrderDetailsEntity orderDetail) {
        return orderDetailsRepository.save(orderDetail);
    }

    public Optional<OrderEntity> findById(Long orderId) {
        return orderRepository.findById(orderId); // Asegúrate de que el repositorio esté configurado correctamente
    }

    public void markAsReceived(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("SENT".equals(order.getStatus())) {
            order.setStatus("RECEIVED");
            orderRepository.save(order);
        }
    }

    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
    }

    public List<OrderEntity> findByUserEmail(String userEmail) {
        return orderRepository.findByUserEmail(userEmail); // Buscar pedidos por email del usuario
    }

    public void updateOrderStatus(Long orderId, String status) {
        // Verifica que la orden exista
        Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderEntity order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Orden no encontrada con ID: " + orderId);
        }
    }

}
