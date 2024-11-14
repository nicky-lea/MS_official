package org.edu.usco.pw.ms_official.controller;
// Importaciones necesarias para la clase MainController
import org.edu.usco.pw.ms_official.model.UserEntity;  // Modelo que representa a un usuario
import org.edu.usco.pw.ms_official.service.UserService;  // Servicio para manejar la lógica de negocio relacionada con usuarios
import org.springframework.beans.factory.annotation.Autowired;  // Anotación para la inyección de dependencias
import org.springframework.stereotype.Controller;  // Anotación que indica que esta clase es un controlador en Spring MVC
import org.springframework.ui.Model;  // Clase utilizada para agregar atributos que se pasarán a la vista
import org.springframework.web.bind.annotation.*;  // Importa las anotaciones necesarias para los controladores de Spring

@Controller  // Define la clase como un controlador de Spring MVC
public class MainController {

    @Autowired  // Inyección de dependencia del servicio UserService
    private UserService userService;

    // Mapea la ruta "/index" y devuelve la vista "index"
    @GetMapping("/index")
    public String showIndex() {
        return "index";  // Devuelve el archivo "index.html" (o su equivalente en la carpeta de vistas)
    }

    // Mapea la ruta "/contacto" y devuelve la vista de contacto en una carpeta subnivel "/no_subpages"
    @GetMapping("/contacto")
    public String showNoPages() {
        return "/no_subpages/contacto";  // Redirige a "no_subpages/contacto.html"
    }

    // Mapea la ruta "/contact" y redirige también a la vista de contacto
    @GetMapping("/contact")
    public String showContact() {
        return "no_subpages/contacto";  // Redirige a "no_subpages/contacto.html" (ruta similar a la anterior)
    }

    // Mapea la ruta "/iniciar" para mostrar el formulario de inicio de sesión
    @GetMapping("/iniciar")
    public String showLogin() {
        return "login";  // Devuelve el archivo "login.html" para la vista de inicio de sesión
    }

    // Mapea la ruta "/register" para mostrar el formulario de registro de usuario
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());  // Añade un objeto UserEntity vacío al modelo para el formulario
        return "register";  // Devuelve el archivo "register.html" (vista para el registro de usuario)
    }

    // Mapea la ruta "/register" para procesar el formulario de registro usando el método POST
    @PostMapping("/register")
    public String registerUser(@RequestParam("cc") Long userId,  // Recibe los parámetros del formulario
                               @RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("phone") String phone,
                               @RequestParam("address") String address) {
        try {
            // Llama al servicio para registrar al usuario con los parámetros proporcionados
            userService.registerUser(userId, name, email, password, phone, address);
            return "redirect:/iniciar";  // Si el registro es exitoso, redirige al login
        } catch (Exception e) {
            return "error";  // Si ocurre un error, muestra la vista "error"
        }
    }

}


