package com.odilo.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("agus.varela10@gmail.com");
        contact.setName("Agustin Varela");
        contact.setUrl("https://github.com/agusvarela/odilo-interview");

        Info info = new Info()
                .title("Odilo Interview API")
                .version("1.0")
                .contact(contact)
                .termsOfService(null)
                .license(null);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
