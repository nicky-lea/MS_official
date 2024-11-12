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
        return cartRepository.findAll(); // Devuelve todos los elementos de la tabla `cart`
    }


    // Obtener el carrito de un usuario usando su cédula
    public List<CartEntity> getCartItemsByUserCc(Long usercc) {
        UserEntity user = userRepository.findUserByCc(usercc); // Encuentra el usuario por su cédula
        if (user != null) {
            return cartRepository.findByUser(user); // Devuelve los elementos del carrito del usuario
        } else {
            return new ArrayList<>(); // Retorna una lista vacía si no se encuentra el usuario
        }
    }


    public void addProductToCart(UserEntity user, Long productId, int quantity) {
        // Verificar si el producto existe
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verificar si el producto ya está en el carrito
        CartEntity cartItem = cartRepository.findByUserAndProduct(user, product)
                .orElse(null);

        // Si el producto ya está en el carrito, aumentamos la cantidad
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Si no está en el carrito, lo agregamos
            cartItem = new CartEntity();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedDate(LocalDateTime.now());
        }

        // Guardar el carrito con el producto
        cartRepository.save(cartItem);
    }

    // Obtener todos los elementos del carrito de un usuario
    public List<CartEntity> getCartByUsercc(Long userCc) {
        return cartRepository.findByUserCc(userCc);
    }

    public void updateCart(Long cartItemId, Integer quantity, String detail) {
        // Buscar el artículo del carrito en la base de datos
        Optional<CartEntity> cartItemOpt = cartRepository.findById(cartItemId);

        if (cartItemOpt.isPresent()) {
            CartEntity cartItem = cartItemOpt.get();

            // Verificar si la cantidad es válida (por ejemplo, mayor que 0)
            if (quantity > 0) {
                // Actualizar los valores del artículo
                cartItem.setQuantity(quantity);
                cartItem.setDetails(detail);

                // Guardar los cambios en el artículo del carrito
                cartRepository.save(cartItem);
            } else {
                // Si la cantidad no es válida, puedes optar por eliminar el artículo del carrito
                cartRepository.delete(cartItem);
            }
        } else {
            throw new RuntimeException("Cart item not found for ID: " + cartItemId);
        }
    }

    public List<CartEntity> getCartItemsForUser(Long userCc) {
        return cartRepository.findByUserCc(userCc);
    }

    // Método para obtener los artículos del carrito para un usuario
    public BigDecimal getSubtotal(Long userCc) {
        // Obtener los artículos del carrito del usuario
        List<CartEntity> cartItems = cartRepository.findByUserCc(userCc);

        // Calcular el subtotal (sumar el precio de los productos * cantidad)
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método para obtener el costo de envío
    public BigDecimal getShippingCost() {
        return BigDecimal.valueOf(8500); // Costo de envío fijo
    }

    // Método para obtener el total (subtotal + envío)
    public BigDecimal getTotal(Long userCc) {
        BigDecimal subtotal = getSubtotal(userCc);
        BigDecimal shippingCost = getShippingCost();
        return subtotal.add(shippingCost);
    }

    public String formatAmount(BigDecimal amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");  // No muestra decimales si son ceros
        return formatter.format(amount);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        // Verifica si el elemento del carrito existe antes de eliminarlo
        if (cartRepository.existsById(cartItemId)) {
            cartRepository.deleteById(cartItemId); // Elimina el elemento del carrito
        } else {
            throw new RuntimeException("Cart item not found"); // Lanza una excepción si el elemento no existe
        }
    }

    @Transactional
    public void clearCartForUser(Long userCc) {
        List<CartEntity> cartItems = getCartItemsForUser(userCc);
        cartRepository.deleteAll(cartItems);
    }
}
