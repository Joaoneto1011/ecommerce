package com.joaoneto.ecommerce.exceptions;

public class NomeUsuarioNaoEncontradoException extends RuntimeException {

    public NomeUsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
