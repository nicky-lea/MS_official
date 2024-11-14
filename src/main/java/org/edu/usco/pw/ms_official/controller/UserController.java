package org.edu.usco.pw.ms_official.controller;

import org.edu.usco.pw.ms_official.model.*;
import org.edu.usco.pw.ms_official.repository.OrderDetailsRepository;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.edu.usco.pw.ms_official.repository.UserRepository;
import org.edu.usco.pw.ms_official.service.CartService;
import org.edu.usco.pw.ms_official.service.OrderService;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/user")  // Define la ruta base para todos los endpoints de este controlador como "/user"
@Controller  // Anota la clase como un controlador de Spring MVC
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageSource messageSource;

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

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

        int totalPrice = 0;  // Usamos int para el total
        for (CartEntity item : cartItems) {
            int itemTotal = item.getProduct().getPrice().intValue() * item.getQuantity();  // Multiplicamos en int
            totalPrice += itemTotal;  // Acumulamos el total en int
        }


        // Calcular el subtotal, costo de envío y total
        BigDecimal subtotal = cartService.getSubtotal(userCc);
        BigDecimal shippingCost = cartService.getShippingCost();
        BigDecimal total = cartService.getTotal(userCc);

        // Pasar los productos y los detalles del usuario al modelo
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user.get());
        model.addAttribute("subtotal", cartService.formatAmount(subtotal));
        model.addAttribute("shippingCost", cartService.formatAmount(shippingCost));
//        model.addAttribute("total", cartService.formatAmount(total));
        model.addAttribute("total", totalPrice);
        return "user/preview"; // Vista de previsualización
    }

    @PostMapping("/makeorder")
    public String makeOrder(RedirectAttributes redirectAttributes,Locale locale, Model model, @RequestParam("userCc") Long userCc,
                            @RequestParam("name") String name,
                            @RequestParam("email") String email,
                            @RequestParam("phone") String phone,
                            @RequestParam("shippingAddress") String shippingAddress,
                            @RequestParam("totalPrice") int totalPrice) {

        // Verificar si el usuario existe
        Optional<UserEntity> userOpt = userRepository.findBycc(userCc);
        if (userOpt.isEmpty()) {
            String errorMessage = messageSource.getMessage("user_not_found", null, locale);
            model.addAttribute("error", errorMessage);
            return "redirect:/user/cart";
        }

        UserEntity user = userOpt.get();

        // Verificar si ya hay un pedido pendiente para este usuario
        Optional<OrderEntity> pendingOrder = orderService.findPendingOrderByUser(user);
        if (pendingOrder.isPresent()) {
            String errorMessage = messageSource.getMessage("pending_order", null, locale);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/user/cart";
        }

        // Crear la orden
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setName(name);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PENDING");
        order.setTotal(totalPrice);

        // Guardar la orden en la base de datos
        order = orderService.saveOrder(order);

        // Obtener los productos del carrito para el usuario
        List<CartEntity> cartItems = cartService.getCartItemsForUser(userCc);

        // Crear los detalles de la orden y guardarlos
        // Crear los detalles de la orden y guardarlos
        for (CartEntity cartItem : cartItems) {
            OrderDetailsEntity orderDetail = new OrderDetailsEntity();
            orderDetail.setOrder(order);  // Asociar el detalle con la orden
            orderDetail.setProduct(cartItem.getProduct());  // Asociar el producto del carrito
            orderDetail.setQuantity(cartItem.getQuantity());  // Establecer la cantidad
            orderDetail.setUnitPrice(cartItem.getProduct().getPrice());  // Establecer el precio unitario
            orderDetail.setDetails(cartItem.getDetails());

            // Guardar el detalle en la base de datos
            orderService.saveOrderDetail(orderDetail);  // Asegúrate de que este método guarda en la BD
        }


        // Limpiar el carrito después de realizar el pedido
        cartService.clearCartForUser(userCc);

        // Redirigir al usuario a la página de confirmación de pedido
        model.addAttribute("order", order);
        return "redirect:/user/order?orderId=" + order.getId();  // Página de confirmación
    }

    @GetMapping("/order")
    public String showOrderConfirmation(@RequestParam("orderId") Long orderId, Model model) {
        // Aquí obtienes el pedido confirmado y lo pasas a la vista de confirmación
        Optional<OrderEntity> order = orderService.findById(orderId);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "/user/orderconfirmation";  // Vista de confirmación del pedido
        }
        model.addAttribute("error", "No se pudo encontrar el pedido.");
        return "redirect:/user/cart";  // Redirige si no se encuentra el pedido
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


    // Muestra los pedidos del usuario actual
    @GetMapping("/orders")
    public String adminOrders(@RequestParam(required = false) Long userCc,
                              @RequestParam(required = false) Long orderId,
                              @RequestParam(required = false) String sort,
                              Model model) {
        List<OrderEntity> orders = new ArrayList<>();

        // Si se proporciona userCc, busca los pedidos de ese usuario
        if (userCc != null) {
            orders = orderService.findByUserCc(userCc); // Método para encontrar pedidos por userCc
        }
        // Si se proporciona orderId, busca el pedido con ese ID
        else if (orderId != null) {
            Optional<OrderEntity> orderOptional = orderService.findById(orderId); // Busca el pedido por ID
            if (orderOptional.isPresent()) {
                orders = Collections.singletonList(orderOptional.get()); // Agrega el pedido si existe
            } else {
                model.addAttribute("message", "No se encontró el pedido con ID: " + orderId);
            }
        }
        // Si no se proporcionan parámetros de filtrado, obtiene todos los pedidos
        else {
            orders = orderService.getAllOrders(); // Método para obtener todos los pedidos
        }

        // Ordena los pedidos según el criterio seleccionado
        if ("id".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(OrderEntity::getId));
        } else if ("cc".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(order -> order.getUser().getCc()));
        }

        // Añadimos los pedidos al modelo para que estén disponibles en la vista
        model.addAttribute("orders", orders);

        return "/user/orders"; // Renderiza la vista orders.html para el admin
    }

    @PostMapping("/markAsReceived/{Id}")
    public String markAsReceived(@PathVariable Long Id) {
        orderService.markAsReceived(Id);
        return "redirect:/user/orders"; // O cualquier URL donde desees mostrar la lista de órdenes
    }

    @PostMapping("/cancelOrder/{Id}")
    public String cancelOrder(@PathVariable Long Id) {
        orderService.cancelOrder(Id);
        return "redirect:/user/orders"; // O cualquier URL donde desees mostrar la lista de órdenes
    }


}
