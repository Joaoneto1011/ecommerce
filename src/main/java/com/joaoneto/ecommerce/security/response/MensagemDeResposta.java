package com.joaoneto.ecommerce.security.response;

import lombok.Data;

@Data
public class MensagemDeResposta {

    private String mensagem;

    public MensagemDeResposta(String mensagem) {
        this.mensagem = mensagem;
    }
}
