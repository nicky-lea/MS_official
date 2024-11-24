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

/**
 * Servicio encargado de manejar la lógica de negocio relacionada con el carrito de compras.
 * Proporciona métodos para agregar, actualizar, eliminar productos en el carrito, y calcular el subtotal.
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene los productos en el carrito de un usuario dado su número de cédula.
     *
     * @param usercc Número de cédula del usuario.
     * @return Lista de {@link CartEntity} con los productos en el carrito del usuario.
     */
    public List<CartEntity> getCartItemsByUserCc(Long usercc) {
        UserEntity user = userRepository.findUserByCc(usercc);
        if (user != null) {
            return cartRepository.findByUser(user);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Agrega un producto al carrito de un usuario o actualiza la cantidad si ya existe.
     *
     * @param user Usuario que desea agregar el producto al carrito.
     * @param productId ID del producto a agregar.
     * @param quantity Cantidad de producto a agregar.
     */
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

    /**
     * Actualiza la cantidad y detalles de un artículo en el carrito.
     * Si la cantidad es 0 o negativa, elimina el artículo del carrito.
     *
     * @param cartItemId ID del artículo en el carrito a actualizar.
     * @param quantity Nueva cantidad del artículo.
     * @param detail Detalle adicional sobre el artículo.
     */
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

    /**
     * Obtiene los artículos en el carrito de un usuario dado su número de cédula.
     *
     * @param userCc Número de cédula del usuario.
     * @return Lista de {@link CartEntity} con los productos en el carrito del usuario.
     */
    public List<CartEntity> getCartItemsForUser(Long userCc) {
        return cartRepository.findByUserCc(userCc);
    }

    /**
     * Calcula el subtotal de los productos en el carrito de un usuario.
     *
     * @param userCc Número de cédula del usuario.
     * @return Subtotal de los productos en el carrito.
     */
    public BigDecimal getSubtotal(Long userCc) {
        List<CartEntity> cartItems = cartRepository.findByUserCc(userCc);

        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el costo de envío fijo.
     *
     * @return Costo de envío fijo de 8500.
     */
    public BigDecimal getShippingCost() {
        return BigDecimal.valueOf(8500);
    }

    /**
     * Formatea una cantidad de dinero a una cadena con formato de miles.
     *
     * @param amount Monto a formatear.
     * @return Monto formateado como cadena.
     */
    public String formatAmount(BigDecimal amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(amount);
    }

    /**
     * Elimina un artículo del carrito de compras.
     *
     * @param cartItemId ID del artículo en el carrito a eliminar.
     */
    @Transactional
    public void removeCartItem(Long cartItemId) {
        if (cartRepository.existsById(cartItemId)) {
            cartRepository.deleteById(cartItemId);
        } else {
            throw new RuntimeException("Cart item not found");
        }
    }

    /**
     * Limpia todos los artículos del carrito de un usuario dado su número de cédula.
     *
     * @param userCc Número de cédula del usuario cuyo carrito se va a limpiar.
     */
    @Transactional
    public void clearCartForUser(Long userCc) {
        List<CartEntity> cartItems = getCartItemsForUser(userCc);
        cartRepository.deleteAll(cartItems);
    }
}