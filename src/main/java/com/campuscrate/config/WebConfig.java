package com.campuscrate.config;

import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String[] allowedOrigins;

    public WebConfig(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        // Render assigns a different subdomain when a frontend service is recreated.
        // Keep configured origins and safely support Campus Crate Render deployments.
        this.allowedOrigins = Stream.concat(
                Arrays.stream(allowedOrigins.split("\\s*,\\s*")),
                Stream.of("https://dbms-frontend-*.onrender.com", "http://localhost:*"))
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // SSLCommerz posts the customer back from the gateway's own origin. These
        // callback routes do not use browser credentials and validate the payment
        // server-side, so they must not be limited to the frontend origin.
        registry.addMapping("/api/food/payments/sslcommerz/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
