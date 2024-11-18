package org.edu.usco.pw.ms_official.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Configura los beans relacionados con la internacionalización y la codificación de la aplicación.
     * Esta clase define cómo manejar la localización, la carga de mensajes y la codificación de caracteres en la aplicación.
     */
    @Bean
    public MessageSource messageSource() {
        /**
         * Crea y configura un MessageSource para la internacionalización de mensajes.
         *
         * @return un MessageSource configurado con la ubicación de los mensajes y la codificación por defecto.
         */
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        /**
         * Crea un resolver de localización que utiliza la sesión para mantener el idioma.
         *
         * @return un SessionLocaleResolver configurado con el idioma predeterminado en español.
         */
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(new Locale("es"));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        /**
         * Crea un interceptor para permitir el cambio de idioma a través del parámetro "lang" en la URL.
         *
         * @return un LocaleChangeInterceptor configurado con el parámetro de cambio de idioma.
         */
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * Añade el interceptor de cambio de idioma al registro de interceptores de la aplicación.
         *
         * @param registry el registro de interceptores donde se agrega el nuevo interceptor.
         */
        registry.addInterceptor(localeChangeInterceptor());
    }


}