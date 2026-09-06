package com.joaoneto.ecommerce.security.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitacaoDeCadastro {

    @Schema(description = "Nome de usuário desejado", example = "joaoneto")
    @NotBlank
    @Size(min = 3, max = 20)
    private String nomeUsuario;

    @Schema(description = "Email do usuário", example = "joao@exemplo.com")
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Schema(description = "Senha de acesso do usuário", example = "senha123")
    @NotBlank
    @Size(min = 6, max = 40)
    private String senha;


}