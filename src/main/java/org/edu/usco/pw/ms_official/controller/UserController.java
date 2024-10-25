package org.edu.usco.pw.ms_official.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    @GetMapping("/user")
    public String index() {
        return "user/index";
    }
}
