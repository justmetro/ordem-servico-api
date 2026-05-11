package com.pedro.ordemservico.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ordem de Serviço API")
                        .version("v1.0.0")
                        .description("API REST corporativa para gestão de ordens de serviço, com PostgreSQL, Flyway, auditoria, paginação, métricas e regras de negócio.")
                        .contact(new Contact()
                                .name("Pedro Oliveira")
                                .url("https://github.com/justmetro/ordem-servico-api")));
    }
}
