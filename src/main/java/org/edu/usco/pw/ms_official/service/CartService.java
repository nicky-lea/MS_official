package org.edu.usco.pw.ms_official.service;
import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.CartRepository;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<CartEntity> getAllCartItems(UserEntity user) {
        return cartRepository.findAll();
    }

    public List<CartEntity> getCartItemsByUserCc(Long usercc) {
        UserEntity user = userRepository.findUserByCc(usercc);
        if (user != null) {
            return cartRepository.findByUser(user);
        } else {
            return new ArrayList<>();
        }
    }

    public void addProductToCart(UserEntity user, Long productId, int quantity) {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartEntity cartItem = cartRepository.findByUserAndProduct(user, product)
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {

            cartItem = new CartEntity();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedDate(LocalDateTime.now());
        }

        cartRepository.save(cartItem);
    }


    public List<CartEntity> getCartByUsercc(Long userCc) {
        return cartRepository.findByUserCc(userCc);
    }

    public void updateCart(Long cartItemId, Integer quantity, String detail) {
        Optional<CartEntity> cartItemOpt = cartRepository.findById(cartItemId);

        if (cartItemOpt.isPresent()) {
            CartEntity cartItem = cartItemOpt.get();

            if (quantity > 0) {

                cartItem.setQuantity(quantity);
                cartItem.setDetails(detail);
                cartRepository.save(cartItem);
            } else {
                cartRepository.delete(cartItem);
            }
        } else {
            throw new RuntimeException("Cart item not found for ID: " + cartItemId);
        }
    }

    public List<CartEntity> getCartItemsForUser(Long userCc) {
        return cartRepository.findByUserCc(userCc);
    }


    public BigDecimal getSubtotal(Long userCc) {

        List<CartEntity> cartItems = cartRepository.findByUserCc(userCc);

        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getShippingCost() {
        return BigDecimal.valueOf(8500);
    }

    public BigDecimal getTotal(Long userCc) {
        BigDecimal subtotal = getSubtotal(userCc);
        BigDecimal shippingCost = getShippingCost();
        return subtotal.add(shippingCost);
    }

    public String formatAmount(BigDecimal amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(amount);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        if (cartRepository.existsById(cartItemId)) {
            cartRepository.deleteById(cartItemId);
        } else {
            throw new RuntimeException("Cart item not found");
        }
    }

    @Transactional
    public void clearCartForUser(Long userCc) {
        List<CartEntity> cartItems = getCartItemsForUser(userCc);
        cartRepository.deleteAll(cartItems);
    }
}
