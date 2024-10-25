package org.edu.usco.pw.ms_official.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class UserController {

    @GetMapping
    public String index() {
        return "user/index";
    }

    @GetMapping("contacto")
    public String contact() {
        return "user/contacto";
    }

    @GetMapping("productos")
    public String products() {
        return "user/productos";
    }

}
