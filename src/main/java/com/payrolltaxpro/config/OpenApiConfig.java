package com.payrolltaxpro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI payrollTaxOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("PayrollTax Pro API")
                        .description("Multi-Company SaaS Payroll Engine with progressive tax calculation, BPJS simulation, overtime calculation, and complete payroll management.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("PayrollTax Pro Team")
                                .email("support@payrolltax.local")
                                .url("https://payrolltax.local"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8081/api")
                                .description("Docker Development Server"),
                        new Server()
                                .url("https://api.payrolltax.local")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token authentication. Use the /auth/login endpoint to get a token. Format: Bearer {token}")
                        ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/auth/**", "/health/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder()
                .group("tenants")
                .pathsToMatch("/tenants/**")
                .build();
    }

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group("employees")
                .pathsToMatch("/employees/**")
                .build();
    }

    @Bean
    public GroupedOpenApi payrollApi() {
        return GroupedOpenApi.builder()
                .group("payroll")
                .pathsToMatch("/payroll/**")
                .build();
    }

    @Bean
    public GroupedOpenApi salaryStructureApi() {
        return GroupedOpenApi.builder()
                .group("salary-structures")
                .pathsToMatch("/salary-structures/**")
                .build();
    }

    @Bean
    public GroupedOpenApi taxBracketApi() {
        return GroupedOpenApi.builder()
                .group("tax-brackets")
                .pathsToMatch("/tax-brackets/**")
                .build();
    }
}
