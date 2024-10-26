package org.edu.usco.pw.ms_official.controller;

import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.Rol;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String index() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String user(Model model) {
        List<UserEntity> users = userService.getAllUsers();
        List<Map<String, Object>> userList = new ArrayList<>();
        for (UserEntity user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user", user);
            userMap.put("roles", user.getRole().getName());
            userList.add(userMap);
        }
        model.addAttribute("userList", userList);
        return "admin/users"; // Esta es la vista que se renderizará
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


}
