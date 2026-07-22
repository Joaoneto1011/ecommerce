package com.joaoneto.ecommerce.security.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitacaoDeLogin {

    @Schema(description = "Nome de usuário cadastrado", example = "joaoneto")
    @NotBlank
    private String nomeUsuario;

    @Schema(description = "Senha de acesso do usuário", example = "senha123")
    @NotBlank
    private String senha;
}