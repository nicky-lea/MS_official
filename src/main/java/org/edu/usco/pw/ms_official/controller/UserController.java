package org.edu.usco.pw.ms_official.controller;


import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private ProductService productService;

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



}
