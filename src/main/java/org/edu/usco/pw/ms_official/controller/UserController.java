package org.edu.usco.pw.ms_official.controller;


import jakarta.servlet.http.HttpSession;
import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.edu.usco.pw.ms_official.service.CartService;
import org.edu.usco.pw.ms_official.service.OrderService;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/user")  // Define la ruta base para todos los endpoints de este controlador como "/user"
@Controller  // Anota la clase como un controlador de Spring MVC
public class UserController {

    // Inyección de dependencias para los servicios de productos, usuarios, carrito y repositorios
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    // Maneja las solicitudes GET para la página principal del usuario
    @GetMapping
    public String index() {
        return "/user/index";  // Redirige a la vista de inicio del usuario
    }

    // Maneja las solicitudes GET para la vista del carrito
    @GetMapping("/cart")
    public String showCart(Model model, Principal principal) {
        // Obtener el correo electrónico del usuario actual (principal)
        String userEmail = principal.getName();

        // Obtener el usuario basado en su correo electrónico
        UserEntity user = userService.findByEmail(userEmail);

        // Si el usuario no existe, lanza una excepción
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Obtener la cédula del usuario (se asume que el método getCc() devuelve la cédula)
        Long userCc = user.getCc();

        // Obtener los elementos del carrito basados en la cédula del usuario
        List<CartEntity> cartItems = cartService.getCartItemsByUserCc(userCc);

        // Calcular el total del carrito
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Generar la cadena de IDs de productos
        String productIds = cartItems.stream()
                .map(item -> String.valueOf(item.getProduct().getId()))
                .collect(Collectors.joining(","));

        // Añadir los elementos del carrito, total y los IDs de productos al modelo
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);  // Agregar el total al modelo
        model.addAttribute("productIds", productIds);  // Agregar los IDs de productos como una cadena
        model.addAttribute("userCc", user.getCc());

        return "user/cart";  // Redirige a la vista de Thymeleaf para el carrito
    }


    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return "redirect:/user/cart"; // Redirige a la vista del carrito bajo el mapeo de usuario
    }


    // Maneja las solicitudes GET para obtener detalles de un producto
    @GetMapping("/product/{id}")
    @ResponseBody  // Indica que el resultado se enviará directamente en la respuesta HTTP
    public ProductEntity getProductDetails(@PathVariable Long id) {
        // Busca el producto por ID y devuelve null si no se encuentra
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return product;  // Devuelve el producto si existe
        }
        return new ProductEntity();  // Devuelve un objeto vacío si no se encuentra
    }

    // Método para agregar un producto al carrito
    @PostMapping("/addtocart")
    public String addProductToCart(@RequestParam Long productId, @RequestParam int quantity,
                                   Principal principal, RedirectAttributes redirectAttributes) {
        // Obtener el correo del usuario actual
        String userEmail = principal.getName();

        // Obtener el usuario a partir de su correo electrónico
        UserEntity user = userService.findByEmail(userEmail);

        // Obtener el producto por su ID, lanzando excepción si no se encuentra
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Añadir el producto al carrito del usuario
        cartService.addProductToCart(user, productId, quantity);

        // Añadir un mensaje para la próxima vista indicando que el producto fue añadido
        redirectAttributes.addFlashAttribute("message", "Producto añadido al carrito");
        redirectAttributes.addFlashAttribute("productName", product.getName());
        redirectAttributes.addFlashAttribute("quantity", quantity);


        return "redirect:/user/products";  // Redirige a la vista de productos
    }

//    @PostMapping("/update")
//    public String updateCart(@RequestParam Long userCc,
//                             @RequestParam Map<Long, Integer> quantities,
//                             @RequestParam Map<Long, String> details) {
//        cartService.updateCart(userCc, quantities, details);  // Llamar al servicio para actualizar el carrito
//        return "redirect:/user/cart?userCc=" + userCc;  // Redirigir a la misma página
//    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam List<Integer> quantities,
                             @RequestParam List<String> details,
                             @RequestParam List<Long> cartItemIds,
                             @RequestParam("userCc") Long userCc,
                             RedirectAttributes redirectAttributes) {

        // Verificar que las listas tengan el mismo tamaño
        if (quantities.size() != details.size() || quantities.size() != cartItemIds.size()) {
            redirectAttributes.addFlashAttribute("error", "Mismatch between quantities, details, and cart item IDs.");
            return "redirect:/user/cart";
        }

        // Obtener el usuario
        Optional<UserEntity> user = userRepository.findBycc(userCc);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/user/cart";
        }

        // Iterar sobre los productos en el carrito y actualizarlos
        for (int i = 0; i < quantities.size(); i++) {
            Long cartItemId = cartItemIds.get(i);
            Integer quantity = quantities.get(i);
            String detail = details.get(i);

            // Lógica para actualizar el carrito
            cartService.updateCart(cartItemId, quantity, detail);
        }

        redirectAttributes.addFlashAttribute("success", "Cart updated successfully.");
        return "redirect:/user/cart";
    }


    @GetMapping("/preview")
    public String previewOrder(Model model, @RequestParam("userCc") Long userCc) {
        // Obtener los productos del carrito de acuerdo al usuario
        List<CartEntity> cartItems = cartService.getCartItemsForUser(userCc);

        // Obtener los detalles del usuario
        Optional<UserEntity> user = userRepository.findBycc(userCc);
        if (user.isEmpty()) {
            return "redirect:/user/cart";
        }

        // Pasar los productos y los detalles del usuario al modelo
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user.get());

        return "user/preview"; // Vista de previsualización
    }

    // Maneja las solicitudes GET para la página de contacto
    @GetMapping("contacto")
    public String contact() {
        return "user/contacto";  // Redirige a la vista de contacto
    }

    // Lista los productos y los muestra en la vista de productos
    @GetMapping("products")
    public String listProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        model.addAttribute("productList", products);  // Añade la lista de productos al modelo
        return "user/productos";  // Redirige a la vista de productos
    }

    // Muestra el perfil del usuario actual
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        // Obtiene el usuario basado en su correo electrónico
        UserEntity user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);  // Añade el usuario al modelo
        return "/user/profile";  // Redirige a la vista del perfil
    }

    // Muestra el formulario de edición para el usuario especificado por correo electrónico
    @GetMapping("/edit/{email}")
    public String showEditForm(@PathVariable("email") String email, Model model) {
        try {
            // Busca el usuario basado en el correo
            UserEntity user = userService.getUserByEmail(email);
            model.addAttribute("user", user);  // Añade el usuario al modelo
            return "/user/edit";  // Redirige a la vista de edición del usuario
        } catch (Exception e) {
            return "/error/404";  // Muestra una página de error si el usuario no es encontrado
        }
    }

    // Actualiza el perfil del usuario basado en el correo
    @PostMapping("/edit")
    public String updateUserByEmail(@RequestParam("email") String email, @ModelAttribute UserEntity userUpdates) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el email especificado");
        }

        UserEntity existingUser = optionalUser.get();

        // Actualiza los campos del usuario con los valores proporcionados
        existingUser.setName(userUpdates.getName());
        existingUser.setAddress(userUpdates.getAddress());
        existingUser.setEmail(userUpdates.getEmail());
        existingUser.setPhone(userUpdates.getPhone());

        userRepository.save(existingUser);  // Guarda el usuario actualizado en la base de datos
        return "redirect:/user/profile?email=" + existingUser.getEmail();  // Redirige al perfil del usuario
    }

    @Autowired
    private OrderService orderService;

    // Muestra los pedidos del usuario actual
    @GetMapping("/orders")
    public String userOrders(Model model, Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName());
        List<OrderEntity> orders = orderService.getOrdersByUserId(user.getCc());
        model.addAttribute("orders", orders);  // Añade los pedidos al modelo
        return "user/orders";  // Redirige a la vista de pedidos del usuario
    }

    // Marca un pedido como recibido
    @PostMapping("/orders/{orderId}/received")
    public String markOrderAsReceived(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, "delivered");  // Actualiza el estado del pedido a "delivered"
        return "redirect:/user/orders";  // Redirige a la página de pedidos del usuario
    }

}
