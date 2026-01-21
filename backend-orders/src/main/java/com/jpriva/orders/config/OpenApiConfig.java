package com.jpriva.orders.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Orders API")
                        .version("0.0.1")
                        .description("Backend orders and inventory")
                        .contact(new Contact()
                                .name("Support")
                                .email("jpriva@outlook.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    @Bean
    public OperationCustomizer customizeRoles() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);

            if (preAuthorize != null) {
                String description = operation.getDescription() == null ? "" : operation.getDescription();
                String roleInfo = "<br/><strong>Security:</strong> " + preAuthorize.value();

                operation.setDescription(description + roleInfo);
            }
            return operation;
        };
    }
}