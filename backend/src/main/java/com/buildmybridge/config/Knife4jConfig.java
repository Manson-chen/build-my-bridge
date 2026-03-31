package com.buildmybridge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置类
 *
 * 提供 OpenAPI 3.0 文档和 Knife4j UI
 * 访问地址: http://localhost:8081/api/doc.html (开发环境)
 * 或     http://localhost:8080/api/doc.html (使用反向代理)
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 OpenAPI 信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildMyBridge API")
                        .version("0.1.0")
                        .description("Feishu 机器人与 Dify 应用集成平台 API 文档")
                        .termsOfService("https://github.com/buildmybridge")
                        .contact(new Contact()
                                .name("BuildMyBridge Team")
                                .url("https://github.com/buildmybridge")
                                .email("support@buildmybridge.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("本地开发环境"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("反向代理环境"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Token (在 Authorization 请求头中使用 Bearer 前缀)")));
    }
}
