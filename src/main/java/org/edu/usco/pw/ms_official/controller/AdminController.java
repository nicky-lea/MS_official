package org.edu.usco.pw.ms_official.controller;

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

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String index() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String user(Model model) {
        return loadUserList(model, userService.getAllUsers());
    }

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
            users = userService.getAllUsers(); // Método para obtener todos los usuarios
        }

        return loadUserList(model, users);
    }

    private String loadUserList(Model model, List<UserEntity> users) {
        List<Map<String, Object>> userList = new ArrayList<>();
        for (UserEntity user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user", user);

            // Obtener todos los nombres de roles y unirlos en una cadena
            String roleNames = user.getRoles().stream()
                    .map(Rol::getName) // Obtener el nombre del rol
                    .collect(Collectors.joining(", ")); // Unir los nombres en una sola cadena

            userMap.put("roles", roleNames);
            userList.add(userMap);
        }
        model.addAttribute("userList", userList);
        return "admin/users"; // Renderiza la vista users.html para el admin
    }

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

        return loadUserList(model, users);
    }

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (ProductEntity product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("product", product);
            productList.add(productMap);
        }

        model.addAttribute("productList", productList);
        return "admin/products"; // Esta es la vista que se renderizará
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "admin/new_product"; // Vista para crear un nuevo producto
    }

    @PostMapping("/products")
    public String saveProduct(@ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Product: " + product);
        if (file != null && !file.isEmpty()) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("src/main/resources/static/img/" + filename);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/img/" + filename);
            System.out.println("Filename: " + file.getOriginalFilename());

        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/edit_product"; // Vista para editar un producto existente
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Actualizando producto con ID: " + id);
        System.out.println("Nombre del archivo: " + (file != null ? file.getOriginalFilename() : "Ninguno"));
        // Llama al servicio para actualizar el producto
        productService.updateProduct(id, product, file);
        return "redirect:/admin/products"; // Redirige a la lista de productos
    }
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products"; // Redirigir a la lista de productos
    }

    @GetMapping("/orders")
    public String adminOrders(@RequestParam(required = false) Long userCc,
                              @RequestParam(required = false) Long orderId,
                              @RequestParam(required = false) String sort,
                              Model model) {
        List<OrderEntity> orders = new ArrayList<>();

        if (userCc != null) {
            orders = orderService.findByUserCc(userCc); // Método para encontrar pedidos por userCc
        } else if (orderId != null) {
            Optional<OrderEntity> orderOptional = orderService.findById(orderId); // Busca el pedido por ID
            if (orderOptional.isPresent()) {
                orders = Collections.singletonList(orderOptional.get()); // Agrega el pedido si existe
            } else {
                model.addAttribute("message", "No se encontró el pedido con ID: " + orderId);
            }
        } else {
            orders = orderService.getAllOrders(); // Método para obtener todos los pedidos
        }

        if ("id".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(OrderEntity::getId));
        } else if ("cc".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(order -> order.getUser().getCc()));
        }

        model.addAttribute("orders", orders);
        return "admin/orders"; // Renderiza la vista orders.html para el admin
    }

    @PostMapping("/orders/markAsSent")
    public String markOrderAsSent(@RequestParam Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status); // Implementa la lógica de actualización en el servicio
        return "redirect:/admin/orders"; // Redirige de vuelta a la lista de pedidos
    }


}
