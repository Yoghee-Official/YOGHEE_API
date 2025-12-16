package com.lagavulin.yoghee.config;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BEARER_AUTH";

    @Value("${yoghee.domain}")
    private String domain;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Project Yoghee API")
                                            .version("0.1.2")
                                            .description("251216"))
                            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                            .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                                    new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                            )
                            .servers(List.of(new Server().url(domain)));
    }
}