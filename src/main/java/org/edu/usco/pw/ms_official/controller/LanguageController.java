package org.edu.usco.pw.ms_official.controller;


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

// Versión Detallada
@Controller
@RequestMapping("/")
@Slf4j
public class LanguageController {

    private final LocaleResolver localeResolver;

    @Autowired
    public LanguageController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @GetMapping("/change-language")
    public String changeLanguage(
            @RequestParam String lang,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // Validar el idioma
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

            // Establecer el nuevo locale
            localeResolver.setLocale(request, response, newLocale);
            log.info("Idioma cambiado exitosamente a: {}", lang);

            // Obtener la página de referencia
            String referer = request.getHeader("Referer");
            String redirect = referer != null ? referer : "/";

            return "redirect:" + redirect;

        } catch (Exception e) {
            log.error("Error al cambiar el idioma: {}", e.getMessage());
            return "redirect:/";
        }
    }

    // Opcional: Endpoint para obtener el idioma actual
    @GetMapping("/current-language")
    @ResponseBody
    public String getCurrentLanguage(HttpServletRequest request) {
        return localeResolver.resolveLocale(request).getLanguage();
    }
}