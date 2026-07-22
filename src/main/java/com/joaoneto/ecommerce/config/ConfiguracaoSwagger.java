package com.joaoneto.ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

@Configuration
public class ConfiguracaoSwagger {

    private static final List<String> ORDEM_DAS_TAGS = List.of(
            "APIs de Autenticação",
            "APIs de Categoria",
            "APIs de Produto",
            "APIs de Carrinho",
            "APIs de Endereço",
            "APIs de Pedido"
    );

    @Bean
    public OpenAPI personalizarOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Insira o token JWT obtido no endpoint de login (/api/autenticacao/entrar). Formato: apenas o token, sem o prefixo 'Bearer'.");

        SecurityRequirement bearerRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("API Ecommerce")
                        .version("1.0")
                        .description("""
                                API REST para uma plataforma de e-commerce, com gerenciamento de produtos, \
                                categorias, carrinho de compras, endereços, pedidos e autenticação via JWT.

                                **Autenticação:** a maioria dos endpoints exige um token JWT. Faça login em \
                                `/api/autenticacao/entrar` e use o botão **Authorize** para informar o token \
                                nas próximas requisições.

                                **Perfis:** endpoints em `/api/administrador/**` exigem perfil de administrador.
                                """)
                        .contact(new Contact()
                                .name("Joao Neto")
                                .email("neto31510@gmail.com")
                                .url("https://github.com/Joaoneto1011")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório do projeto no GitHub")
                        .url("https://github.com/Joaoneto1011/ecommerce"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", bearerScheme))
                .addSecurityItem(bearerRequirement)
                .tags(List.of(
                        new Tag().name("APIs de Autenticação").description("APIs públicas para login, cadastro e sessão do usuário"),
                        new Tag().name("APIs de Categoria").description("APIs para gerenciamento de categorias"),
                        new Tag().name("APIs de Produto").description("APIs para gerenciamento e consulta de produtos"),
                        new Tag().name("APIs de Carrinho").description("APIs para gerenciamento do carrinho de compras"),
                        new Tag().name("APIs de Endereço").description("APIs para gerenciamento dos endereços do usuário"),
                        new Tag().name("APIs de Pedido").description("APIs para gerenciamento de pedidos")
                ));
    }

    @Bean
    public GlobalOpenApiCustomizer ordenarTagsCustomizer() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.getTags().sort(Comparator.comparingInt(tag -> ORDEM_DAS_TAGS.indexOf(tag.getName())));
            }
        };
    }
}