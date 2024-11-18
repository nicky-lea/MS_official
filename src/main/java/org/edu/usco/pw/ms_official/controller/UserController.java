package org.edu.usco.pw.ms_official.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/user")
@Controller
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

    /**
     * Muestra la vista de contacto para el usuario.
     *
     * Este método devuelve la vista correspondiente a la página de contacto donde los usuarios
     * pueden obtener información de contacto o formularios para comunicarse con el soporte.
     *
     * @return La vista de contacto.
     */
    @Operation(summary = "Vista de contacto", description = "Muestra la página de contacto donde los usuarios pueden obtener información de contacto o enviar mensajes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vista de contacto cargada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al cargar la página de contacto")
    })
    @GetMapping("contacto")
    public String contact() {
        return "user/contacto";
    }

    /**
     * Muestra la lista de productos, ordenados según la opción seleccionada.
     *
     * Este método permite obtener y mostrar todos los productos disponibles, con la opción de
     * ordenarlos según el criterio seleccionado por el usuario (por ejemplo, por nombre o precio).
     * Si no se especifica una opción de orden, se usa la opción por defecto "name:asc" (orden ascendente por nombre).
     *
     * @param sortOption Opción de orden que define el criterio de orden y dirección (por ejemplo, "name:asc").
     *                   Si no se proporciona, se utiliza "name:asc" por defecto.
     * @param model Modelo para pasar los productos ordenados a la vista.
     * @return La vista que muestra la lista de productos ordenados.
     */
    @Operation(summary = "Lista de productos", description = "Muestra todos los productos disponibles, ordenados según el criterio seleccionado por el usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos cargada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al cargar los productos")
    })
    @GetMapping("products")
    public String listProducts(@RequestParam(value = "sortOption", required = false, defaultValue = "name:asc") String sortOption,
                               Model model) {
        String[] sortParams = sortOption.split(":");
        String sortBy = sortParams[0];
        String order = sortParams[1];

        List<ProductEntity> products = productService.getAllProductsSorted(sortBy, order);
        model.addAttribute("productList", products);
        model.addAttribute("currentSortOption", sortOption);
        return "user/productos";
    }

    /**
     * Muestra el perfil del usuario autenticado.
     *
     * Este método obtiene la información del usuario autenticado, utilizando su correo electrónico para buscar sus datos
     * en el sistema, y luego los pasa al modelo para mostrarlos en la vista del perfil del usuario.
     *
     * @param model Modelo para pasar los datos del usuario a la vista.
     * @param principal Información del usuario autenticado.
     * @return La vista del perfil del usuario.
     */
    @Operation(summary = "Perfil de usuario", description = "Muestra la información del perfil del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de usuario cargado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al cargar el perfil del usuario")
    })
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {

        UserEntity user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "/user/profile";
    }

    /**
     * Muestra el formulario de edición del perfil del usuario.
     *
     * Este método busca al usuario por su correo electrónico para obtener sus detalles y mostrar un formulario
     * para editar su perfil. Si el usuario no se encuentra, se redirige a una página de error 404.
     *
     * @param email Correo electrónico del usuario que se va a editar.
     * @param model Modelo para pasar los datos del usuario a la vista.
     * @return La vista para editar el perfil del usuario o una página de error si no se encuentra el usuario.
     */
    @Operation(summary = "Formulario de edición de perfil", description = "Muestra el formulario para editar el perfil del usuario identificado por su correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario de edición cargado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al cargar el formulario de edición")
    })
    @GetMapping("/edit/{email}")
    public String showEditForm(@PathVariable("email") String email, Model model) {
        try {
            UserEntity user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            return "/user/edit";
        } catch (Exception e) {
            return "/error/404";
        }
    }

    /**
     * Actualiza los detalles de un usuario existente utilizando su correo electrónico.
     *
     * Este método recibe los datos actualizados de un usuario y los guarda en la base de datos.
     * Si no se encuentra un usuario con el correo electrónico proporcionado, se lanza una excepción.
     *
     * @param email Correo electrónico del usuario cuyo perfil se va a actualizar.
     * @param userUpdates Datos actualizados del usuario, como nombre, dirección, correo electrónico y teléfono.
     * @return Redirección al perfil del usuario con los cambios guardados.
     * @throws IllegalArgumentException si no se encuentra un usuario con el correo electrónico especificado.
     */
    @Operation(summary = "Actualizar detalles del perfil del usuario", description = "Actualiza los datos de un usuario existente en la base de datos utilizando su correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el correo electrónico proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error al actualizar los datos del usuario")
    })
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

        userRepository.save(existingUser);
        return "redirect:/user/profile?email=" + existingUser.getEmail();
    }

    /**
     * Muestra la página principal del usuario.
     *
     * @return Vista principal del usuario.
     */
    @Operation(summary = "Página principal del usuario", description = "Muestra la vista principal del usuario.")
    @ApiResponse(responseCode = "200", description = "Vista principal del usuario cargada correctamente")
    @GetMapping
    public String index() {
        return "/user/index";  // Redirige a la vista de inicio del usuario
    }

    /**
     * Muestra la vista del carrito de compras del usuario.
     * Este método obtiene los elementos del carrito de compras del usuario autenticado,
     * calcula el precio total de los artículos en el carrito y devuelve la vista correspondiente.
     *
     * @param model Modelo para la vista, utilizado para pasar los atributos a la página.
     * @param principal Información del usuario autenticado, contiene el correo electrónico del usuario.
     * @return Vista del carrito de compras, que contiene los productos en el carrito, el precio total y otros detalles relacionados.
     * @throws RuntimeException Si no se encuentra el usuario en la base de datos.
     */
    @Operation(summary = "Vista del carrito de compras", description = "Muestra los elementos en el carrito de compras del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vista del carrito cargada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al cargar el carrito")
    })
    @GetMapping("/cart")
    public String showCart(Model model, Principal principal) {
        String userEmail = principal.getName();
        UserEntity user = userService.findByEmail(userEmail);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Long userCc = user.getCc();

        List<CartEntity> cartItems = cartService.getCartItemsByUserCc(userCc);

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String productIds = cartItems.stream()
                .map(item -> String.valueOf(item.getProduct().getId()))
                .collect(Collectors.joining(","));

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("productIds", productIds);
        model.addAttribute("userCc", user.getCc());

        return "user/cart";
    }

    /**
     * Elimina un artículo del carrito de compras del usuario.
     * Este método elimina el artículo identificado por su ID del carrito de compras.
     * Una vez que el artículo es eliminado, redirige al usuario a la vista del carrito actualizado.
     *
     * @param id ID del artículo en el carrito que se desea eliminar.
     * @return Redirección a la vista del carrito de compras actualizada.
     */
    @Operation(summary = "Eliminar artículo del carrito", description = "Elimina un artículo del carrito de compras del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo eliminado correctamente del carrito"),
            @ApiResponse(responseCode = "404", description = "Artículo no encontrado en el carrito"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar el artículo del carrito")
    })
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return "redirect:/user/cart";
    }

    /**
     * Obtiene los detalles de un producto por su ID.
     * Este método devuelve los detalles de un producto en formato JSON. Si el producto con el ID
     * proporcionado existe, se devuelve el producto; de lo contrario, se devuelve un objeto vacío.
     *
     * @param id ID del producto cuyo detalle se desea obtener.
     * @return Detalles del producto si se encuentra; un objeto vacío si no se encuentra.
     */
    @Operation(summary = "Obtener detalles del producto", description = "Devuelve los detalles de un producto dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del producto obtenidos correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/product/{id}")
    @ResponseBody
    public ProductEntity getProductDetails(@PathVariable Long id) {
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return product;
        }
        return new ProductEntity();
    }

    /**
     * Agrega un producto al carrito de compras del usuario.
     *
     * Este método maneja la adición de un producto al carrito de compras. Recibe el ID del producto y
     * la cantidad deseada, valida la existencia del producto y lo agrega al carrito del usuario autenticado.
     * Luego, redirige al usuario a la página de productos con un mensaje de confirmación.
     *
     * @param productId ID del producto a añadir al carrito.
     * @param quantity Cantidad del producto a añadir.
     * @param principal Información del usuario autenticado.
     * @param redirectAttributes Atributos para redirigir con un mensaje de confirmación.
     * @return Redirección a la página de productos con un mensaje de éxito.
     */
    @Operation(summary = "Añadir producto al carrito", description = "Agrega un producto al carrito de compras del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto añadido al carrito correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al añadir el producto al carrito")
    })
    @PostMapping("/addtocart")
    public String addProductToCart(@RequestParam Long productId, @RequestParam int quantity,
                                   Principal principal, RedirectAttributes redirectAttributes) {
        String userEmail = principal.getName();
        UserEntity user = userService.findByEmail(userEmail);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartService.addProductToCart(user, productId, quantity);

        redirectAttributes.addFlashAttribute("message", "Producto añadido al carrito");
        redirectAttributes.addFlashAttribute("productName", product.getName());
        redirectAttributes.addFlashAttribute("quantity", quantity);

        return "redirect:/user/products";
    }

    /**
     * Actualiza los productos en el carrito de compras del usuario.
     * Este método recibe listas de cantidades, detalles y IDs de los productos en el carrito,
     * junto con el número de cédula del usuario, y actualiza los elementos correspondientes en el carrito.
     * Si las listas no coinciden en tamaño o si el usuario no es encontrado, se redirige al usuario con un mensaje de error.
     * Si la actualización es exitosa, se redirige con un mensaje de éxito.
     *
     * @param quantities Lista de cantidades de los productos en el carrito.
     * @param details Lista de detalles asociados a cada producto en el carrito.
     * @param cartItemIds Lista de IDs de los productos en el carrito.
     * @param userCc Número de cédula del usuario cuyo carrito será actualizado.
     * @param redirectAttributes Atributos para redirigir con mensajes de éxito o error.
     * @return Redirección al carrito de compras con un mensaje de éxito o error.
     */
    @Operation(summary = "Actualizar carrito de compras", description = "Actualiza los productos en el carrito de compras del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Desajuste entre las listas de cantidades, detalles e IDs de carrito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al actualizar el carrito")
    })
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam List<Integer> quantities,
                             @RequestParam List<String> details,
                             @RequestParam List<Long> cartItemIds,
                             @RequestParam("userCc") Long userCc,
                             RedirectAttributes redirectAttributes) {

        if (quantities.size() != details.size() || quantities.size() != cartItemIds.size()) {
            redirectAttributes.addFlashAttribute("error", "Mismatch between quantities, details, and cart item IDs.");
            return "redirect:/user/cart";
        }

        Optional<UserEntity> user = userRepository.findBycc(userCc);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/user/cart";
        }

        for (int i = 0; i < quantities.size(); i++) {
            Long cartItemId = cartItemIds.get(i);
            Integer quantity = quantities.get(i);
            String detail = details.get(i);

            cartService.updateCart(cartItemId, quantity, detail);
        }

        redirectAttributes.addFlashAttribute("success", "Cart updated successfully.");
        return "redirect:/user/cart";
    }

    /**
     * Muestra una vista previa del pedido antes de la compra.
     *
     * Este método recupera los elementos del carrito del usuario mediante su número de cédula,
     * calcula el total del pedido, y prepara la vista con los detalles del carrito, el subtotal,
     * los costos de envío y el total. Si el usuario no es encontrado, redirige al carrito.
     *
     * @param model El modelo que se utiliza para pasar los atributos a la vista.
     * @param userCc El número de cédula del usuario que realiza la compra.
     * @return La vista con la vista previa del pedido.
     */
    @Operation(summary = "Vista previa del pedido", description = "Muestra una vista previa del pedido con los detalles del carrito, subtotal, costos de envío y total.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vista previa cargada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado, redirigiendo al carrito"),
            @ApiResponse(responseCode = "500", description = "Error al cargar la vista previa")
    })
    @GetMapping("/preview")
    public String previewOrder(Model model, @RequestParam("userCc") Long userCc) {
        List<CartEntity> cartItems = cartService.getCartItemsForUser(userCc);

        Optional<UserEntity> user = userRepository.findBycc(userCc);
        if (user.isEmpty()) {
            return "redirect:/user/cart";
        }

        int totalPrice = 0;
        for (CartEntity item : cartItems) {
            int itemTotal = item.getProduct().getPrice().intValue() * item.getQuantity();
            totalPrice += itemTotal;
        }

        BigDecimal subtotal = cartService.getSubtotal(userCc);
        BigDecimal shippingCost = cartService.getShippingCost();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user.get());
        model.addAttribute("subtotal", cartService.formatAmount(subtotal));
        model.addAttribute("shippingCost", cartService.formatAmount(shippingCost));
        model.addAttribute("total", totalPrice);

        return "user/preview";
    }

    /**
     * Crea un nuevo pedido para un usuario, utilizando la información del carrito de compras.
     *
     * Este método primero verifica si el usuario existe y si no tiene un pedido pendiente. Luego, crea una nueva orden con la información proporcionada,
     * incluye los detalles de los productos del carrito de compras y finalmente limpia el carrito del usuario.
     *
     * @param redirectAttributes Atributos de redirección para mensajes flash.
     * @param locale Localización de los mensajes de error.
     * @param model Modelo de la vista.
     * @param userCc Número de cédula del usuario.
     * @param name Nombre del usuario para el pedido.
     * @param email Correo electrónico del usuario para el pedido.
     * @param phone Teléfono del usuario para el pedido.
     * @param shippingAddress Dirección de envío para el pedido.
     * @param totalPrice Precio total del pedido.
     * @return Redirección a la vista del pedido recién creado si todo sale bien, o al carrito de compras si hay errores.
     */
    @Operation(summary = "Crear un pedido", description = "Crea un nuevo pedido para un usuario con la información proporcionada desde el carrito de compras.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido creado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido pendiente ya existente"),
            @ApiResponse(responseCode = "500", description = "Error al crear el pedido")
    })
    @PostMapping("/makeorder")
    public String makeOrder(RedirectAttributes redirectAttributes, Locale locale, Model model, @RequestParam("userCc") Long userCc,
                            @RequestParam("name") String name,
                            @RequestParam("email") String email,
                            @RequestParam("phone") String phone,
                            @RequestParam("shippingAddress") String shippingAddress,
                            @RequestParam("totalPrice") int totalPrice) {

        Optional<UserEntity> userOpt = userRepository.findBycc(userCc);
        if (userOpt.isEmpty()) {
            String errorMessage = messageSource.getMessage("user_not_found", null, locale);
            model.addAttribute("error", errorMessage);
            return "redirect:/user/cart";
        }

        UserEntity user = userOpt.get();

        Optional<OrderEntity> pendingOrder = orderService.findPendingOrderByUser(user);
        if (pendingOrder.isPresent()) {
            String errorMessage = messageSource.getMessage("pending_order", null, locale);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/user/cart";
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setName(name);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PENDING");
        order.setTotal(totalPrice);

        order = orderService.saveOrder(order);

        List<CartEntity> cartItems = cartService.getCartItemsForUser(userCc);

        for (CartEntity cartItem : cartItems) {
            OrderDetailsEntity orderDetail = new OrderDetailsEntity();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getProduct().getPrice());
            orderDetail.setDetails(cartItem.getDetails());

            orderService.saveOrderDetail(orderDetail);
        }

        cartService.clearCartForUser(userCc);

        model.addAttribute("order", order);
        return "redirect:/user/order?orderId=" + order.getId();
    }

    /**
     * Muestra la confirmación de un pedido.
     *
     * Este método recupera el pedido con el ID especificado. Si el pedido existe, se muestra la confirmación con los detalles del pedido.
     * Si no se encuentra el pedido, se redirige al usuario al carrito con un mensaje de error.
     *
     * @param orderId ID del pedido que se desea consultar.
     * @param model Modelo de la vista para enviar datos a la plantilla.
     * @return La vista de confirmación del pedido o redirección al carrito si no se encuentra el pedido.
     */
    @Operation(summary = "Mostrar confirmación del pedido", description = "Recupera y muestra la confirmación de un pedido basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmación del pedido mostrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al mostrar la confirmación del pedido")
    })
    @GetMapping("/order")
    public String showOrderConfirmation(@RequestParam("orderId") Long orderId, Model model) {

        Optional<OrderEntity> order = orderService.findById(orderId);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "/user/orderconfirmation";
        }
        model.addAttribute("error", "No se pudo encontrar el pedido.");
        return "redirect:/user/cart";
    }

    /**
     * Muestra los pedidos de un usuario.
     *
     * Este método recupera los pedidos de un usuario según diferentes criterios como el ID de pedido, el estado y el criterio de ordenamiento. Si no se especifica un ID de pedido, se muestran todos los pedidos del usuario.
     * Además, los pedidos se pueden filtrar por estado y ordenar por ID, total o fecha. Si no se encuentran pedidos, se muestra un mensaje informando al usuario.
     *
     * @param orderId ID del pedido que se desea consultar (opcional).
     * @param sort Criterio de ordenación de los pedidos (opcional). Puede ser "id", "total" o "date".
     * @param status Estado de los pedidos que se desea filtrar (opcional).
     * @param model Modelo de la vista para enviar los datos a la plantilla.
     * @param principal Información del usuario autenticado.
     * @return La vista que muestra los pedidos del usuario o un mensaje de error si no se encuentran pedidos.
     */
    @Operation(summary = "Mostrar los pedidos de un usuario", description = "Recupera y muestra los pedidos de un usuario basado en varios criterios como ID de pedido, estado y ordenación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos mostrados correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se encontraron pedidos para el usuario")
    })
    @GetMapping("/orders")
    public String userOrders(@RequestParam(required = false) Long orderId,
                             @RequestParam(required = false) String sort,
                             @RequestParam(required = false) String status,
                             Model model, Principal principal) {

        String userEmail = principal.getName();

        Optional<UserEntity> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            List<OrderEntity> orders = new ArrayList<>();

            if (orderId != null) {
                Optional<OrderEntity> orderOptional = orderService.findById(orderId);
                if (orderOptional.isPresent() &&
                        orderOptional.get().getUser().getEmail().equals(userEmail)) {

                    orders = Collections.singletonList(orderOptional.get());
                } else {
                    model.addAttribute("message", "No se encontró el pedido con ID: " + orderId
                            + " o no pertenece a este usuario.");
                }
            } else {

                orders = orderService.findByUserEmail(userEmail);
            }

            if (status != null && !status.isEmpty()) {
                orders = orders.stream()
                        .filter(order -> order.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }

            if ("id".equalsIgnoreCase(sort)) {
                orders.sort(Comparator.comparing(OrderEntity::getId));
            } else if ("total".equalsIgnoreCase(sort)) {
                orders.sort(Comparator.comparingInt(OrderEntity::getTotal));
            } else if ("date".equalsIgnoreCase(sort)) {
                orders.sort(Comparator.comparing(OrderEntity::getOrderDate));
            }

            if (orders.isEmpty()) {
                model.addAttribute("message", "No tienes pedidos.");
            }

            model.addAttribute("orders", orders);

            return "/user/orders";
        } else {
            model.addAttribute("error", "Usuario no encontrado");
            return "error";
        }
    }

    /**
     * Marca un pedido como recibido.
     *
     * Este método cambia el estado de un pedido a "Recibido". Después de marcarlo, el usuario es redirigido a la lista de pedidos.
     *
     * @param Id El ID del pedido que se desea marcar como recibido.
     * @return Redirige a la lista de pedidos del usuario.
     */
    @Operation(summary = "Marcar un pedido como recibido", description = "Este método cambia el estado de un pedido a 'Recibido' y redirige a la lista de pedidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como recibido correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PostMapping("/markAsReceived/{Id}")
    public String markAsReceived(@PathVariable Long Id) {
        orderService.markAsReceived(Id);
        return "redirect:/user/orders";
    }

    /**
     * Cancela un pedido.
     *
     * Este método cambia el estado de un pedido a "Cancelado". Después de cancelarlo, el usuario es redirigido a la lista de pedidos.
     *
     * @param Id El ID del pedido que se desea cancelar.
     * @return Redirige a la lista de pedidos del usuario.
     */
    @Operation(summary = "Cancelar un pedido", description = "Este método cambia el estado de un pedido a 'Cancelado' y redirige a la lista de pedidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PostMapping("/cancelOrder/{Id}")
    public String cancelOrder(@PathVariable Long Id) {
        orderService.cancelOrder(Id);
        return "redirect:/user/orders";
    }


}
