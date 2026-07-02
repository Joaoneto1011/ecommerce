package com.joaoneto.ecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RespostaDeInformacoesUsuario {

    private Long id;
    private String nomeUsuario;
    private String jwtToken;
    private List<String> perfis;

    public RespostaDeInformacoesUsuario(Long id, String nomeUsuario, List<String> perfis) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.perfis = perfis;
    }
}
