package org.edu.usco.pw.ms_official.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping
    public String showIndex() {
        return "index";
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
    public String showRegister() {
        return "register";
    }
}
