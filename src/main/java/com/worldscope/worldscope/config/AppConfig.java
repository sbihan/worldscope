package com.worldscope.worldscope.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration Spring de l'application WorldScope.
 * <p>
 * Déclare les beans partagés entre les couches service et controller.
 * </p>
 */
@Configuration
public class AppConfig {

    /**
     * Fournit un {@link RestTemplate} partagé pour les appels à l'API externe.
     *
     * @return instance de {@link RestTemplate} prête à l'emploi
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
