package com.joaoneto.ecommerce.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UtilitarioDeImagem {

    private final String urlImagem;

    public UtilitarioDeImagem(@Value("${imagem.base.url}") String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String construirUrl(String nomeImagem) {
        if (nomeImagem == null) {
            return null;
        }
        if (nomeImagem.startsWith("http://") || nomeImagem.startsWith("https://")) {
            return nomeImagem;
        }
        return urlImagem.endsWith("/") ? urlImagem + nomeImagem : urlImagem + "/" + nomeImagem;
    }
}
