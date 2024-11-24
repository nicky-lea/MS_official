package org.edu.usco.pw.ms_official.controller;
// Importaciones necesarias para la clase MainController
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.edu.usco.pw.ms_official.model.UserEntity;  // Modelo que representa a un usuario
import org.edu.usco.pw.ms_official.service.UserService;  // Servicio para manejar la lógica de negocio relacionada con usuarios
import org.springframework.beans.factory.annotation.Autowired;  // Anotación para la inyección de dependencias
import org.springframework.stereotype.Controller;  // Anotación que indica que esta clase es un controlador en Spring MVC
import org.springframework.ui.Model;  // Clase utilizada para agregar atributos que se pasarán a la vista
import org.springframework.web.bind.annotation.*;  // Importa las anotaciones necesarias para los controladores de Spring

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    /**
     * Muestra la página de inicio de la aplicación.
     *
     * @return el nombre de la vista "index".
     */
    @Operation(summary = "Muestra la página de inicio", description = "Este endpoint muestra la página de inicio de la aplicación.")
    @GetMapping("/index")
    public String showIndex() {
        return "index";
    }

    /**
     * Muestra la página de contacto en la carpeta /no_subpages.
     *
     * @return el nombre de la vista de contacto.
     */
    @Operation(summary = "Muestra la página de contacto", description = "Este endpoint muestra la página de contacto de la aplicación.")
    @GetMapping("/contacto")
    public String showNoPages() {
        return "/no_subpages/contacto";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "no_subpages/contacto";
    }

    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @return el nombre de la vista de inicio de sesión.
     */
    @Operation(summary = "Muestra el formulario de inicio de sesión", description = "Este endpoint muestra el formulario para iniciar sesión.")
    @GetMapping("/iniciar")
    public String showLogin() {
        return "login";
    }

    /**
     * Muestra el formulario de registro de usuario.
     *
     * @param model el modelo que se pasa a la vista.
     * @return el nombre de la vista de registro.
     */
    @Operation(summary = "Muestra el formulario de registro", description = "Este endpoint muestra el formulario para el registro de nuevos usuarios.")
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    /**
     * Procesa el formulario de registro de usuario.
     *
     * @param userId el ID del usuario.
     * @param name el nombre del usuario.
     * @param email el correo electrónico del usuario.
     * @param password la contraseña del usuario.
     * @param phone el teléfono del usuario.
     * @param address la dirección del usuario.
     * @return una redirección a la página de inicio de sesión o un mensaje de error.
     */
    @Operation(summary = "Procesa el formulario de registro", description = "Este endpoint procesa el formulario de registro de un nuevo usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso, redirige al inicio de sesión."),
            @ApiResponse(responseCode = "400", description = "Error en el registro del usuario.")
    })
    @PostMapping("/register")
    public String registerUser(@RequestParam("cc") String userId,
                               @RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("phone") String phone,
                               @RequestParam("address") String address,
                               Model model) {
        try {
            validateCC(userId);
            validateName(name);
            validatePassword(password);
            validatePhone(phone);
            validateAddress(address);

            userService.registerUser(Long.parseLong(userId), name, email, password, phone, address);
            return "redirect:/iniciar";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/register";
        }
    }

    private void validateCC(String cc) {
        if (!cc.matches("^\\d+$")) {
            throw new IllegalArgumentException("error.cc.invalid");
        }
    }

    private void validateName(String name) {
        if (!name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new IllegalArgumentException("error.name.invalid");
        }
    }

    private void validatePassword(String password) {
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new IllegalArgumentException("error.password.invalid");
        }
    }


    private void validatePhone(String phone) {
        if (!phone.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("error.phone.invalid");
        }
    }

    private void validateAddress(String address) {
        if (!address.matches("^[a-zA-Z0-9\\s#\\-/áéíóúÁÉÍÓÚñÑ,]+$")) {
            throw new IllegalArgumentException("error.address.invalid");
        }
    }



}



