package com.jusep1983.blackjack.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Blackjack APPI", version = "1.0", description = "API para jugar al Blackjack")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Example errorExample = new Example()
                .summary("Generic error")
                .description("Internal server error example")
                .value("{\"status\":500,\"message\":\"Internal Server Error\",\"data\":null}");

        Example badRequestExample = new Example()
                .summary("Invalid input")
                .description("Example of a bad request")
                .value("{\"status\":400,\"message\":\"Invalid player name\",\"data\":null}");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        // Error 500 reutilizable
                        .addResponses("InternalServerError", new ApiResponse()
                                .description("Internal server error")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType()
                                                .schema(getApiResponseSchema())
                                                .addExamples("internalError", errorExample)
                                ))
                        )

                        // Error 400 reutilizable
                        .addResponses("BadRequest", new ApiResponse()
                                .description("Invalid input data")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType()
                                                .schema(getApiResponseSchema())
                                                .addExamples("badRequest", badRequestExample)
                                ))
                        )
                );
    }

    private Schema<?> getApiResponseSchema() {
        Schema<Object> schema = new ObjectSchema();
        schema.addProperty("status", new IntegerSchema().example(400));
        schema.addProperty("message", new StringSchema().example("Error message"));
        schema.addProperty("data", new ObjectSchema().example(null)); // null o vacío según lo que uses
        return schema;
    }

}
