package org.example.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // corresponds to allowed-origins: '*'
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // corresponds to allowed-methods
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") // corresponds to allowed-headers
                .exposedHeaders("Custom-Header") // corresponds to exposed-headers
                .allowCredentials(true) // corresponds to allow-credentials
                .maxAge(3600); // corresponds to max-age
    }
}
