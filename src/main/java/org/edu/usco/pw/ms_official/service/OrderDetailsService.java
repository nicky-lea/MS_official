package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.model.OrderDetailsEntity;
import org.edu.usco.pw.ms_official.repository.OrderDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public OrderDetailsEntity addOrderDetails(OrderDetailsEntity orderDetailsEntity) {
        return orderDetailsRepository.save(orderDetailsEntity);
    }

    public void deleteOrderDetails(Long orderDetailsId) {
        orderDetailsRepository.deleteById(orderDetailsId);
    }
}
