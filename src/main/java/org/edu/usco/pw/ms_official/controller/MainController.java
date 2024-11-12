package org.edu.usco.pw.ms_official.controller;

import org.edu.usco.pw.ms_official.model.UserEntity;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String showIndex() {
        return "index";
    }



    @GetMapping("/contacto")
    public String showNoPages() {
        return "/no_subpages/contacto";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "no_subpages/contacto";
    }

    @GetMapping("/iniciar")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";  // El archivo HTML para la vista de registro
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("cc") Long userId,
                               @RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("phone") String phone,
                               @RequestParam("address") String address) {
        try {
            userService.registerUser(userId, name, email, password, phone, address);
            return "redirect:/iniciar"; // Redirige al login si el registro fue exitoso
        } catch (Exception e) {
            return "error";
        }
    }

}

