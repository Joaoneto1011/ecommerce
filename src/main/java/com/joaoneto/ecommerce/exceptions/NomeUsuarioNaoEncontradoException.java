package com.joaoneto.ecommerce.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class NomeUsuarioNaoEncontradoException extends UsernameNotFoundException {

    public NomeUsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
