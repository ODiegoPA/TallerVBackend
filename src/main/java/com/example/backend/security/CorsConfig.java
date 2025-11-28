package com.example.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    // configurables por properties:
    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:5173}")
    private String allowedOriginsCsv;

    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethodsCsv;

    @Value("${cors.allowed-headers:Authorization,Content-Type}")
    private String allowedHeadersCsv;

    @Value("${cors.exposed-headers:Authorization}")
    private String exposedHeadersCsv;

    @Value("${cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(split(allowedOriginsCsv));
        cfg.setAllowedMethods(split(allowedMethodsCsv));
        cfg.setAllowedHeaders(split(allowedHeadersCsv));
        cfg.setExposedHeaders(split(exposedHeadersCsv));
        cfg.setAllowCredentials(allowCredentials);
        // cache preflight (opcional)
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    private List<String> split(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
