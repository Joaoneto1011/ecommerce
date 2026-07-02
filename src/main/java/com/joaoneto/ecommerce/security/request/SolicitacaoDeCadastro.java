package com.joaoneto.ecommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SolicitacaoDeCadastro {

    @NotBlank
    @Size(min = 3, max = 20)
    private String nomeUsuario;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> perfis;

    @NotBlank
    @Size(min = 6, max = 40)
    private String senha;


}
