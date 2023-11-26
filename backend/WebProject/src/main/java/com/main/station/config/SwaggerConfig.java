package com.main.station.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.OAS_30)
//                .useDefaultResponseMessages(false)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.main.station.controller"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("teamproject")
//                .description("teamproject 8조 API")
//                .build();
//    }

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
