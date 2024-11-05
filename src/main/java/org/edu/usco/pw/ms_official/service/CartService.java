package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.CartRepository;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public CartEntity addToCart(CartEntity cartEntity) {
        return cartRepository.save(cartEntity);
    }

    public List<CartEntity> getCartByUser(UserEntity user) {
        return cartRepository.findByUser(user);
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
