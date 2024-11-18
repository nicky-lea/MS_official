package org.edu.usco.pw.ms_official.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
@Controller
@RequestMapping("/")
@Slf4j
public class LanguageController {

    private final LocaleResolver localeResolver;

    @Autowired
    public LanguageController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Endpoint para cambiar el idioma de la aplicación.
     *
     * Este método permite cambiar el idioma de la interfaz de usuario entre los idiomas soportados.
     * Los idiomas disponibles son: español (es), inglés (en) y portugués (pt).
     * Si el idioma especificado no es soportado, se utiliza español por defecto.
     *
     * @param lang Idioma a establecer (es, en, pt).
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @return Redirige a la página referenciada o a la raíz si no hay referencia.
     * @throws Exception Si ocurre un error al cambiar el idioma.
     */
    @Operation(summary = "Cambiar idioma de la aplicación", description = "Permite cambiar el idioma de la interfaz de usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Idioma cambiado con éxito"),
            @ApiResponse(responseCode = "400", description = "Idioma no soportado")
    })
    @GetMapping("/change-language")
    public String changeLanguage(
            @RequestParam String lang,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Locale newLocale;
            switch (lang.toLowerCase()) {
                case "es":
                    newLocale = new Locale("es");
                    break;
                case "en":
                    newLocale = new Locale("en");
                    break;
                case "pt":
                    newLocale = new Locale("pt");
                    break;
                default:
                    log.warn("Idioma no soportado: {}. Usando español por defecto.", lang);
                    newLocale = new Locale("es");
            }

            localeResolver.setLocale(request, response, newLocale);
            log.info("Idioma cambiado exitosamente a: {}", lang);

            String referer = request.getHeader("Referer");
            String redirect = referer != null ? referer : "/";

            return "redirect:" + redirect;

        } catch (Exception e) {
            log.error("Error al cambiar el idioma: {}", e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * Endpoint para obtener el idioma actual de la aplicación.
     *
     * Este método devuelve el idioma actualmente configurado en la aplicación.
     *
     * @param request La solicitud HTTP.
     * @return El idioma actual de la aplicación (por ejemplo, "es", "en", "pt").
     */
    @Operation(summary = "Obtener idioma actual", description = "Devuelve el idioma actualmente configurado en la aplicación.")
    @GetMapping("/current-language")
    @ResponseBody
    public String getCurrentLanguage(HttpServletRequest request) {
        return localeResolver.resolveLocale(request).getLanguage();
    }
}
