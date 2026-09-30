package com.example.assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI assignmentOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Assignment Service API")
                .description("Spring Boot REST API for user management and external API integration")
                .version("1.0.0")
                .contact(new Contact().name("Backend Team").email("backend@example.com")));
    }
}