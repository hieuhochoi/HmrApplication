package com.example.hrmapplication.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearer-jwt";
        final String oauth2SchemeName = "oauth2-keycloak";

        return new OpenAPI()
                .info(new Info()
                        .title("HRM System API")
                        .version("1.0.0")
                        .description("""
                                Human Resource Management System API Documentation
                                
                                ## Authentication
                                
                                Hệ thống sử dụng OAuth2 với Keycloak để xác thực. Có 2 cách để authenticate:
                                
                                1. **OAuth2 (Keycloak)**: Sử dụng OAuth2 flow trong Swagger UI
                                2. **Bearer JWT Token**: Sử dụng JWT token từ Keycloak
                                
                                ## Roles
                                
                                - **ADMIN**: Quản trị viên hệ thống
                                - **HR**: Nhân viên quản lý nhân sự
                                - **MANAGER**: Trưởng phòng
                                - **EMPLOYEE**: Nhân viên
                                
                                ## Lưu ý
                                
                                Hiện tại tất cả endpoints trả về HTML (Thymeleaf views), không phải JSON.
                                Để có REST API JSON, cần chuyển đổi controllers sang @RestController.
                                """)
                        .contact(new Contact()
                                .name("HRM System Development Team")
                                .email("support@hrm-system.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.hrm-system.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(oauth2SchemeName))
                .components(new Components()
                        // JWT Bearer Token
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token từ Keycloak. Lấy token từ: http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token"))
                        // OAuth2 với Keycloak
                        .addSecuritySchemes(oauth2SchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:8080/realms/hrm-realm/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token")
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "User Profile")
                                                        .addString("email", "User Email")
                                                        .addString("roles", "User Roles")))
                                )
                                .description("OAuth2 với Keycloak. Client ID: hrm-client")));
    }
}

