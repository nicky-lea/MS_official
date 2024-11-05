package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.excepciones.ResourceNotFoundException;
import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderEntity createOrder(OrderEntity orderEntity) {
        return orderRepository.save(orderEntity);
    }

    public List<OrderEntity> getOrdersByUserId(Long userCc) {
        UserEntity user = new UserEntity(); // Debes buscar el usuario por ID
        user.setCc(userCc);
        return orderRepository.findByUser(user);
    }

    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public void updateOrderStatus(Long orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll(); // Obtiene todos los pedidos
    }

    public List<OrderEntity> findByUserCc(Long userCc) {
        return orderRepository.findByUser_Cc(userCc); // Implementa el método en el repositorio
    }


    public Optional<OrderEntity> findById(Long orderId) {
        return orderRepository.findById(orderId); // Asegúrate de que el repositorio esté configurado correctamente
    }



}
