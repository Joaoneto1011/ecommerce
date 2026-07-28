package com.joaoneto.ecommerce.security.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RespostaDeInformacoesUsuario {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome de usuário", example = "joaoneto")
    private String nomeUsuario;

    @Schema(description = "Lista de perfis (roles) do usuário", example = "[\"PERFIL_USUARIO\"]")
    private List<String> perfis;

    @Schema(description = "Token JWT para uso em requisições subsequentes (header Authorization: Bearer {token}")
    private String token;

    public RespostaDeInformacoesUsuario(Long id, String nomeUsuario,List<String> perfis, String token) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.perfis = perfis;
        this.token = token;
    }
}