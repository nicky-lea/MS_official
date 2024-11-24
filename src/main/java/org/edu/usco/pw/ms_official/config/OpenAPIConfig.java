package org.edu.usco.pw.ms_official.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "M&S",
                description = "API para gestionar un ecommerce de manualidades, permitiendo realizar operaciones CRUD sobre productos, precios, stock y características. Facilita la gestión de productos de arte, herramientas, materiales y kits para manualidades.",
                contact = @Contact(
                        name = "Soporte MS",
                        email = "adminms@gmail.com"
                )
        )
)
public class OpenAPIConfig {
}