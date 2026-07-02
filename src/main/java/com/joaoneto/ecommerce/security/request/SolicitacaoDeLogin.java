package com.joaoneto.ecommerce.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitacaoDeLogin {

    @NotBlank
    private String nomeUsuario;

    @NotBlank
    private String senha;
}
