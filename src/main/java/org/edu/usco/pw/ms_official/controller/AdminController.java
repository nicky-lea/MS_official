package org.edu.usco.pw.ms_official.controller;
// Importación de las entidades y servicios necesarios para manejar los usuarios, productos y órdenes.
import org.edu.usco.pw.ms_official.model.OrderEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.service.OrderService;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

// El controlador de administración, que gestiona todas las acciones de administración en la aplicación
@Controller
@RequestMapping("/admin")
public class AdminController {

    // Inyección de dependencias para los servicios de usuario, orden y producto
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    // Método que muestra la vista principal del panel de administración
    @GetMapping
    public String index() {
        return "admin/index";
    }

    // Método que muestra la lista de usuarios
    @GetMapping("/users")
    public String user(Model model) {
        return loadUserList(model, userService.getAllUsers()); // Carga la lista de usuarios
    }

    // Método para buscar usuarios por CC, nombre o correo
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam(required = false) String search, Model model) {
        List<UserEntity> users;

        if (search != null && !search.isEmpty()) {
            try {
                Long userCc = Long.parseLong(search);
                users = userService.findByCc(userCc); // Busca por CC
            } catch (NumberFormatException e) {
                // Si no es un número, busca por nombre o email
                users = userService.findByNameOrEmailContaining(search);
            }
        } else {
            users = userService.getAllUsers(); // Obtiene todos los usuarios si no hay filtro
        }

        return loadUserList(model, users); // Carga la lista de usuarios al modelo
    }

    // Método auxiliar para cargar la lista de usuarios al modelo
    private String loadUserList(Model model, List<UserEntity> users) {
        List<Map<String, Object>> userList = new ArrayList<>();
        for (UserEntity user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user", user);

            // Obtener todos los nombres de roles y unirlos en una cadena
            String roleNames = user.getRoles().stream()
                    .map(Rol::getName) // Obtiene el nombre del rol
                    .collect(Collectors.joining(", ")); // Junta los nombres de roles en una sola cadena

            userMap.put("roles", roleNames);
            userList.add(userMap);
        }
        model.addAttribute("userList", userList); // Agrega la lista de usuarios al modelo
        return "admin/users"; // Devuelve la vista con la lista de usuarios
    }

    // Método para ordenar la lista de usuarios por distintos criterios (CC, nombre, email)
    @GetMapping("/users/sort")
    public String sortUsers(@RequestParam(required = false) String sortBy, Model model) {
        List<UserEntity> users;

        if (sortBy != null && !sortBy.isEmpty()) {
            // Llama al servicio para obtener usuarios ordenados
            switch (sortBy) {
                case "cc":
                    users = userService.getAllUsersSortedByCc();
                    break;
                case "name":
                    users = userService.getAllUsersSortedByName();
                    break;
                case "email":
                    users = userService.getAllUsersSortedByEmail();
                    break;
                default:
                    users = userService.getAllUsers(); // Por defecto, sin ordenar
                    break;
            }
        } else {
            users = userService.getAllUsers(); // Sin ordenar
        }

        return loadUserList(model, users); // Carga la lista ordenada de usuarios
    }

    // Inyección del servicio de productos para manejar los productos
    @Autowired
    private ProductService productService;

    // Método para listar los productos
    @GetMapping("/products")
    public String listProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (ProductEntity product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("product", product);
            productList.add(productMap);
        }

        model.addAttribute("productList", productList); // Agrega la lista de productos al modelo
        return "admin/products"; // Devuelve la vista con la lista de productos
    }

    // Método para mostrar el formulario para crear un nuevo producto
    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "admin/new_product"; // Vista para crear un nuevo producto
    }

    // Método para guardar un nuevo producto
    @PostMapping("/products")
    public String saveProduct(@ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Product: " + product);
        if (file != null && !file.isEmpty()) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("src/main/resources/static/img/" + filename);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/img/" + filename); // Guarda la URL de la imagen
            System.out.println("Filename: " + file.getOriginalFilename());
        }
        productService.save(product); // Guarda el producto en la base de datos
        return "redirect:/admin/products"; // Redirige a la lista de productos
    }

    // Método para mostrar el formulario de edición de un producto existente
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/edit_product"; // Vista para editar un producto
    }

    // Método para actualizar un producto
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Actualizando producto con ID: " + id);
        System.out.println("Nombre del archivo: " + (file != null ? file.getOriginalFilename() : "Ninguno"));
        productService.updateProduct(id, product, file); // Llama al servicio para actualizar el producto
        return "redirect:/admin/products"; // Redirige a la lista de productos
    }

    // Método para eliminar un producto
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id); // Elimina el producto
        return "redirect:/admin/products"; // Redirige a la lista de productos
    }

    // Método para listar las órdenes de los usuarios
    @GetMapping("/orders")
    public String adminOrders(@RequestParam(required = false) Long userCc,
                              @RequestParam(required = false) Long orderId,
                              @RequestParam(required = false) String sort,
                              Model model) {
        List<OrderEntity> orders = new ArrayList<>();

        if (userCc != null) {
            orders = orderService.findByUserCc(userCc); // Busca las órdenes por CC de usuario
        } else if (orderId != null) {
            Optional<OrderEntity> orderOptional = orderService.findById(orderId); // Busca una orden por ID
            if (orderOptional.isPresent()) {
                orders = Collections.singletonList(orderOptional.get()); // Agrega la orden si existe
            } else {
                model.addAttribute("message", "No se encontró el pedido con ID: " + orderId);
            }
        } else {
            orders = orderService.getAllOrders(); // Obtiene todas las órdenes si no hay filtro
        }

        if ("id".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(OrderEntity::getId)); // Ordena por ID
        } else if ("cc".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(order -> order.getUser().getCc())); // Ordena por CC
        }

        model.addAttribute("orders", orders); // Agrega las órdenes al modelo
        return "/admin/orders"; // Devuelve la vista con la lista de órdenes
    }

    // Método para marcar una orden como enviada
    @PostMapping("/orders/markAsSent")
    public String markOrderAsSent(@RequestParam Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status); // Actualiza el estado de la orden
        return "redirect:/admin/orders"; // Redirige a la lista de órdenes
    }
}

