package org.edu.usco.pw.ms_official.controller;


import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.edu.usco.pw.ms_official.service.CartService;
import org.edu.usco.pw.ms_official.service.OrderService;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index() {
        return "user/index";
    }

    @GetMapping("contacto")
    public String contact() {
        return "user/contacto";
    }

    @GetMapping("products")
    public String listProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        model.addAttribute("productList", products);
        return "user/productos";
    }

    // Ver perfil del usuario
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName()); // Obtiene el usuario según el email
        model.addAttribute("user", user);
        return "/user/profile"; // Renderiza la vista profile.html
    }
    // Muestra el formulario de edición para el usuario con el correo dado
    @GetMapping("/edit/{email}")
    public String showEditForm(@PathVariable("email") String email, Model model) {
        try {
            UserEntity user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            return "/user/edit";  // Asegúrate de que la plantilla esté en "templates/user/edit.html"
        } catch (Exception e) {
            // Muestra la página de error si el usuario no es encontrado
            return "/error/404";  // Cambia "error/404" según tu vista de error
        }
    }

    @PostMapping("/edit")
    public String updateUserByEmail(@RequestParam("email") String email, @ModelAttribute UserEntity userUpdates) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el email especificado");
        }

        UserEntity existingUser = optionalUser.get();

        existingUser.setName(userUpdates.getName());
        existingUser.setAddress(userUpdates.getAddress());
        existingUser.setEmail(userUpdates.getEmail());
        existingUser.setPhone(userUpdates.getPhone());
        // Otros campos que desees actualizar

        userRepository.save(existingUser);
        return "redirect:/user/profile?email=" + existingUser.getEmail();
    }


    @Autowired
    private OrderService orderService;

    // Ver pedidos del usuario
    @GetMapping("/orders")
    public String userOrders(Model model, Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName());
        List<OrderEntity> orders = orderService.getOrdersByUserId(user.getCc()); // Asumiendo que getCc() devuelve un identificador único
        model.addAttribute("orders", orders);
        return "user/orders"; // Asegúrate de que este archivo exista en "templates/user/orders.html"
    }

    // Actualizar el estado de un pedido
    @PostMapping("/orders/{orderId}/received")
    public String markOrderAsReceived(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, "delivered"); // Cambia el estado a 'delivered'
        return "redirect:/user/orders"; // Redirige a la página de pedidos
    }


    @Autowired
    private CartService cartService;

    @PostMapping("/addtocart")
    public CartEntity addToCart(@RequestBody CartEntity cart) {
        return cartService.addToCart(cart);
    }

    @GetMapping("/{userId}")
    public List<CartEntity> getCartByUser(@PathVariable Long userCc) {
        UserEntity user = new UserEntity(); // Debes buscar el usuario por ID
        user.setCc(userCc);
        return cartService.getCartByUser(user);
    }

    @DeleteMapping("/{cartId}")
    public void removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
    }

}
