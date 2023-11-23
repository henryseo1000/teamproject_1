package com.main.station.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean(name = "swaggerGroupOpenApi")
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("teamproject")
                .pathsToMatch("/**")
                .build();
    }

    @Bean(name = "swaggerSpringShopOpenApi")
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("team_Project")
                        .description("teamproject08 API"));
    }

}
